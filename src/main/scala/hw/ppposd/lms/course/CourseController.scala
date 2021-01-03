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
      accessService.listUserCourseBriefs(userId)
    } ~ pathPrefixId[Course] { courseId => concat (
      (pathEnd & get) {
        getCourse(courseId)
      },
      (path("teachers") & get) {
        accessService.listCourseTeacherBriefs(courseId)
      },
      pathPrefix("tutors") {
        (pathEnd & get) {
          accessService.listCourseTutorBriefs(courseId)
        } ~ (pathEnd & post) {
          complete("")
        } ~ (pathPrefixId[User] & pathEnd & post) { otherUserId =>
          accessService.canManageTutors(userId, courseId).flatMap {
            case true => addTutor(courseId, otherUserId)
            case false => ApiError(403, "not authorized to add tutors")
          }
        } ~ (pathPrefixId[User] & pathEnd & delete) { otherUserId =>
          accessService.canManageTutors(userId, courseId).flatMap {
            case true => deleteTutor(courseId, otherUserId)
            case false => ApiError(403, "not authorized to delete tutors")
          }
        }
      },
      pathPrefix("materials") {
        complete(s"material controller") // todo
      },
      pathPrefix("homeworks") {
        complete(s"homework controller") // todo
      },
    )}
  }

  def getCourse(courseId: Id[Course]): Future[Course] =
    courseRepo.find(courseId).flatMap(assertFound("course"))

  def addTutor(courseId: Id[Course], userId: Id[User]): Future[Unit] = ???

  def deleteTutor(courseId: Id[Course], userId: Id[User]): Future[Unit] = ???
}
