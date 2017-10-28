package rxu.jobcoin.mixer.model

case class MixerRecord (
  depositAddress: String, //this should be unique
  toAddresses: Set[String],
  originalAmount: Double = 0,
  transferredPercent: Double = 0, //represented as a decimal
  status: Status.Value = Status.AWAITING_DEPOSIT
) //Could use some timestamps so you can expire the record if the person never deposits

object Status extends Enumeration {
  val AWAITING_DEPOSIT = Value(0)
  val TRANSFERRING = Value(1)
  val COMPLETE = Value(2)
}
