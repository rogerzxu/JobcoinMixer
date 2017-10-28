package rxu.jobcoin.mixer.dto

import play.api.libs.json.{Format, Json}

case class MixAddressesResponse(
  status: String,
  depositAddress: Option[String] = None,
  error: Option[String] = None,
  invalidAddresses: Option[Set[String]] = None
)

object MixAddressesResponse {
  implicit val mixAddressesResponseFormat: Format[MixAddressesResponse] = Json.format[MixAddressesResponse]
}
