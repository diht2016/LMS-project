package hw.ppposd.lms

import akka.http.scaladsl.server.Route

import scala.concurrent.ExecutionContext

class RootRouting(wiring: RootWiring)(implicit ec: ExecutionContext) extends Controller {
  import wiring._
  def route: Route =
    authController.route ~ authController.userSession { userId => concat(
      courseController.route(userId),
      groupController.route(userId),
      userController.route(userId),
    )}
}
