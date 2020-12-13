package hw.ppposd.lms.user

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class UserController(userRepo: UserRepository)(implicit ec: ExecutionContext) extends Controller {
  def route(userId: Id[User]): Route = {
    pathPrefix("me") {
      get {
        complete("show my info") // todo
      } ~ patch {
        complete("edit my personal info") // todo
      }
    } ~ pathPrefix(Segment) { userId =>
      get {
        complete(s"show other user info, extracted userId = $userId") // todo
      }
    }
  }
}

// todo: add object and specify patch entity model