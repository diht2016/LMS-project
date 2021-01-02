package hw.ppposd.lms.course

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.access.AccessService
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class CourseController(courseRepo: CourseRepository, accessService: AccessService)
                      (implicit ec: ExecutionContext) extends Controller {
  def route(userId: Id[User]): Route = {
    (pathEndOrSingleSlash & get) {
      listCourses(userId)
    } ~ pathPrefix(Segment) { courseId => concat (
      // todo
      complete(s"extracted courseId = $courseId")
    )}
  }

  def listCourses(userId: Id[User]): Future[Seq[Course]] = {
    val courseIdsFuture = accessService.getUserCourseIds(userId)
    courseIdsFuture.flatMap(Future.traverse(_) { courseId =>
      courseRepo.find(courseId)
    }).map(_.flatten)
  }
}
