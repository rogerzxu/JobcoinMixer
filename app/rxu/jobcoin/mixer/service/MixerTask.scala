package rxu.jobcoin.mixer.service

import rxu.jobcoin.mixer.config.Config

import akka.actor.ActorSystem
import com.google.inject.Inject

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class MixerTask @Inject()(
  actorSystem: ActorSystem,
  config: Config,
  mixerService: MixerService
)(
  implicit executionContext: ExecutionContext
){

  actorSystem.scheduler.schedule(
    initialDelay = 10.seconds,
    interval = config.transferInterval.seconds) {
    for {
      _ <- mixerService.checkForDeposits
      _ <- mixerService.transferIncrements
    } yield ()
  }

}
