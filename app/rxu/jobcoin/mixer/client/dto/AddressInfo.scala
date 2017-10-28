package rxu.jobcoin.mixer.client.dto

import play.api.libs.json.Json

case class AddressInfo(
  balance: String,
  transactions: Seq[Transaction]
)

object AddressInfo {
  implicit val addressInfoFormat = Json.format[AddressInfo]
}
