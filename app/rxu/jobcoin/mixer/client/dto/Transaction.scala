package rxu.jobcoin.mixer.client.dto

import play.api.libs.json.Json

import java.time.Instant

case class Transaction (
  timestamp: Instant,
  fromAddress: Option[String],
  toAddress: String,
  amount: String
)

object Transaction {
  implicit val transactionFormat = Json.format[Transaction]
}
