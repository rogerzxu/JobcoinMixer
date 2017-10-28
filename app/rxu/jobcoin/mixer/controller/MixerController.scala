package rxu.jobcoin.mixer.controller

import rxu.jobcoin.mixer.dto.MixAddressesResponse
import rxu.jobcoin.mixer.service.MixerService

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class MixerController @Inject()(
  controllerComponents: ControllerComponents,
  mixerService: MixerService
)(
  implicit executionContext: ExecutionContext
) extends AbstractController(controllerComponents) {

  def index = Action {
    Ok("Jobcoin Mixer")
  }

  def getDepositAddress = Action.async(parse.json) { implicit request =>
    request.body.validate[Seq[String]].fold(
      errors => Future.successful(
        BadRequest(Json.toJson(
          MixAddressesResponse(
            status = "ERROR",
            error = Some("You must send a list of String addresses as mix recipients")
          ))
        )
      ),
      addresses => {
        val nonEmptyAddresses = addresses.filterNot(_.isEmpty).toSet
        if (nonEmptyAddresses.isEmpty) Future.successful(
          BadRequest(Json.toJson(
            MixAddressesResponse(
              status = "ERROR",
              error = Some("You must provide at least 1 non-empty address as mix recipient")
            )
          ))
        )
        else {
          mixerService.getInvalidAddresses(nonEmptyAddresses) map { invalidAddresses =>
            if (invalidAddresses.isEmpty) Ok(Json.toJson(
              MixAddressesResponse(
                status = "OK",
                depositAddress = Some(mixerService.createMixerDepositAddress(nonEmptyAddresses))
              )
            ))
            else Conflict(Json.toJson(
              MixAddressesResponse(
                status = "ERROR",
                error = Some("Address(es) have either been used or were unable to be validated"),
                invalidAddresses = Some(invalidAddresses)
              )
            ))
          }
        }
      }
    )
  }

}
