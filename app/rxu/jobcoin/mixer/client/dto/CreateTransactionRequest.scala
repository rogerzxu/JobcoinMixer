package rxu.jobcoin.mixer.client.dto

import play.api.libs.json.Json

case class CreateTransactionRequest(
  fromAddress: String,
  toAddress: String,
  amount: String
)

object CreateTransactionRequest {
  implicit val createTransactionRequestFormat = Json.format[CreateTransactionRequest]
}
