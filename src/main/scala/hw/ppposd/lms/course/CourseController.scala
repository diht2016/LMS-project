package hw.ppposd.lms.course

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class CourseController(courseRepo: CourseRepository) extends Controller {
  def route(userId: Id[User])(implicit ec: ExecutionContext): Route = {
    pathEndOrSingleSlash {
      get { listCourses(userId) }
    } ~ pathPrefix(Segment) { courseId => concat (
      // todo
      complete(s"extracted courseId = ${courseId}")
    )} ~ complete {
      println(extractUnmatchedPath.toString)
      "1"
    }
  }

  def listCourses(userId: Id[User]): Future[Seq[Course]] = {
    courseRepo.list()
  }

}
