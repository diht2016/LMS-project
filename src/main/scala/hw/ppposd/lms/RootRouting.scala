package hw.ppposd.lms

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Wiring.Controllers._
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.ExecutionContext

object RootRouting extends Controller {
  def route(implicit ec: ExecutionContext): Route =
    authController.route ~ cookie("SESSION") { session =>
      onSuccess(authController.sessionToUserId(session.value)) {
        case Some(userId) => innerRoute(userId)
        case None => complete(401, "invalid session")
      }
    }

  private def innerRoute(userId: Id[User])(implicit ec: ExecutionContext): Route =
    pathPrefix("courses") {
      courseController.route(userId)
    }
}
