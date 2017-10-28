package rxu.jobcoin.mixer.config

import com.google.inject.Singleton
import com.typesafe.config.ConfigFactory

@Singleton
class Config {

  val config = ConfigFactory.load

  val jobcoinApiUrl = config.getString("jobcoin.api.url")
  val fee = config.getDouble("rxu.jobcoin.mixer.fee")
  val transferInterval = config.getInt("rxu.jobcoin.mixer.transferInterval")
  val transferIncrements = config.getDouble("rxu.jobcoin.mixer.transferIncrements")
  val numTransfersForCompletion = (1 / transferIncrements).toInt
  val houseAddress = config.getString("rxu.jobcoin.mixer.houseAddress")
  val revenueAddress = config.getString("rxu.jobcoin.mixer.revenueAddress")

}
