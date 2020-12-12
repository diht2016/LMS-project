package hw.ppposd.lms

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Wiring.Controllers._

import scala.concurrent.ExecutionContext

object RootRouting extends Controller {
  def route(implicit ec: ExecutionContext): Route =
    authController.route ~ authController.userSession { userId => concat(
      courseController.route(userId),
      // groupController.route(userId),
      // userController.route(userId),
    )}
}
