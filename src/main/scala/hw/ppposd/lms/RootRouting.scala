package hw.ppposd.lms

import akka.http.scaladsl.server.Route

import scala.concurrent.ExecutionContext

class RootRouting(wiring: RootWiring)(implicit ec: ExecutionContext) extends Controller {
  import wiring._
  def route: Route =
    pathPrefix("auth") {
      authController.route
    } ~ authController.userSession { userId => concat(
      pathPrefix("courses") {
        courseController.route(userId)
      },
      pathPrefix("group") {
        groupController.route(userId)
      },
      pathPrefix("users") {
        userController.route(userId)
      },
    )}
}
