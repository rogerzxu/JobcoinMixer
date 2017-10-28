package rxu.jobcoin.mixer.client

import rxu.jobcoin.mixer.client.dto.{AddressInfo, CreateTransactionRequest, Transaction}
import rxu.jobcoin.mixer.config.Config

import com.google.inject.Inject
import play.api.libs.json.{JsPath, Json, JsonValidationError}
import play.api.libs.ws.WSClient
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

class JobcoinClient @Inject()(
  ws: WSClient,
  config: Config
)(
  implicit executionContext: ExecutionContext
) {

  def addressInfo(address: String): Future[AddressInfo] = {
    Logger.info(s"Request Address Info for $address")
    val request = ws.url(s"${config.jobcoinApiUrl}/addresses/$address")
      .addHttpHeaders(("accept", "application/json"))
    request.get map { response =>
      Logger.info(s"Received response ${response.body}")
      response.json.validate[AddressInfo].fold(
        errors => throw JobcoinClientException("Unable to parse AddressInfo from Jobcoin API", errors),
        addressInfo => addressInfo
      )
    }
  }

  def listTransactions: Future[Seq[Transaction]] = {
    val request = ws.url(s"${config.jobcoinApiUrl}/transactions")
      .addHttpHeaders(("accept", "application/json"))
    request.get map { response =>
      Logger.info(s"Received response ${response.body}")
      response.json.validate[Seq[Transaction]].fold(
        errors => throw JobcoinClientException("Unable to parse AddressInfo from Jobcoin API", errors),
        transactions => transactions
      )
    }
  }

  def createTransaction(from: String, to: String, amount: String): Future[Unit] = {
    Logger.info(s"Creating transaction from $from to $to for $amount")
    val request = ws.url(s"${config.jobcoinApiUrl}/transactions")
      .addHttpHeaders(("content-type", "application/json"), ("accept", "application/json"))
    val data = CreateTransactionRequest(from, to, amount)
    request.post(Json.toJson(data)) map { response =>
      Logger.info(s"Received response ${response.body}")
      (response.json \ "error").asOpt[String] map { errorMsg =>
        throw TransactionException(errorMsg)
      }
    }
  }

}

case class TransactionException(msg: String) extends Exception(msg)
case class JobcoinClientException(msg: String, errors: Seq[(JsPath, Seq[JsonValidationError])]) extends Exception(msg)
