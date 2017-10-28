package rxu.jobcoin.mixer.service

import rxu.jobcoin.mixer.client.{JobcoinClient, JobcoinClientException, TransactionException}
import rxu.jobcoin.mixer.config.Config
import rxu.jobcoin.mixer.dao.MixerDao
import rxu.jobcoin.mixer.model.{MixerRecord, Status}

import com.google.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import play.api.Logger

import java.util.UUID

class MixerService @Inject()(
  config: Config,
  jobcoinClient: JobcoinClient,
  mixerDao: MixerDao
)(
  implicit executionContext: ExecutionContext
) {

  def createMixerDepositAddress(toAddresses: Set[String]): String = {
    val depositAddress = UUID.randomUUID.toString
    val mixerRecord = MixerRecord(
      depositAddress = depositAddress,
      toAddresses = toAddresses
    )
    mixerDao.put(depositAddress, mixerRecord)
    depositAddress
  }

  //Returns list of invalid addresses (either because the request to jobcoin failed, or they
  //have already been used.
  def getInvalidAddresses(targetAddresses: Set[String]): Future[Set[String]] = {
    Logger.info(s"Validating addresses: $targetAddresses")
    Future.sequence(
      targetAddresses.map { targetAddress =>
        jobcoinClient.addressInfo(targetAddress) map { addressInfo =>
          (targetAddress, addressInfo.transactions.isEmpty)
        } recover {
          case ex: JobcoinClientException => Logger.error(s"Failed to get address info for $targetAddress", ex)
            (targetAddress, false)
        }
      }
    ).map(_.filterNot(_._2).map(_._1))
  }

  def checkForDeposits: Future[Unit] = {
    for {
      addressInfos <- Future.sequence(
        mixerDao.list.filter(_.status == Status.AWAITING_DEPOSIT).map { record =>
          jobcoinClient.addressInfo(record.depositAddress) map { addressInfo =>
            (record.depositAddress, addressInfo)
          }
        }
      )
      depositsSentToHouse <- Future.sequence(
        addressInfos.filter(_._2.balance.toDouble > 0).map { case (depositAddress, addressInfo) =>
          jobcoinClient.createTransaction(
            depositAddress,
            config.houseAddress,
            addressInfo.balance
          ) map (_ => (depositAddress, addressInfo.balance))
        }
      )
    } yield {
      depositsSentToHouse map { case (depositAddress, amount) =>
        val newRecord = mixerDao.get(depositAddress).copy(
          originalAmount = amount.toDouble,
          status = Status.TRANSFERRING
        )
        mixerDao.update(depositAddress, newRecord)
      }
    }
  }

  def transferIncrements: Future[Unit] = {
    val recordsInProgress = mixerDao.list.filter(_.status == Status.TRANSFERRING)
    for {
      _ <- Future.sequence(recordsInProgress flatMap transferToCustomer)
    } yield ()
  }

  private def transferToCustomer(record: MixerRecord): Set[Future[Unit]] = {
    val transferAmount = config.transferIncrements * (1 - config.fee) * record.originalAmount / record.toAddresses.size
    val feeAmount = config.fee * record.originalAmount / record.toAddresses.size / config.numTransfersForCompletion
    record.toAddresses map { toAddress =>
      (for {
        _ <- jobcoinClient.createTransaction(config.houseAddress, toAddress, transferAmount.toString)
        _ <- jobcoinClient.createTransaction(config.houseAddress, config.revenueAddress, feeAmount.toString)
      } yield {
        val updatedRecord = mixerDao.get(record.depositAddress)
        val transferredPercent = updatedRecord.transferredPercent + (config.transferIncrements / record.toAddresses.size)
        val status = if (isComplete(transferredPercent)) Status.COMPLETE else Status.TRANSFERRING
        mixerDao.update(record.depositAddress, updatedRecord.copy(
          transferredPercent = transferredPercent,
          status = status
        ))
        Logger.info(s"Transferred $transferredPercent for $record")
      }) recover {
        case ex: TransactionException => Logger.error(s"Failed to make transaction for $record", ex)
      }
    }
  }

  //Floating point comparison
  private def isComplete(transferredPercent: Double): Boolean = {
    if (Math.abs(1 - transferredPercent) < .00001) true
    else false
  }
}
