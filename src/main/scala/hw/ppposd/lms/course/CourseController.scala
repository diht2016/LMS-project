package hw.ppposd.lms.course

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class CourseController(courseRepo: CourseRepository, accessRepo: AccessRepository, wiring: CourseWiring)
                      (implicit ec: ExecutionContext) extends Controller {
  import wiring._
  def route(userId: Id[User]): Route = {
    (pathEndOrSingleSlash & get) {
      listUserCourses(userId)
    } ~ pathPrefixId[Course] { courseId => concat (
      (pathEnd & get) {
        getCourse(courseId)
      },
      (path("teachers") & get) {
        teacherController.route(userId, courseId)
      },
      pathPrefix("tutors") {
        tutorController.route(userId, courseId)
      },
      pathPrefix("materials") {
        materialController.route(userId, courseId)
      },
      pathPrefix("homeworks") {
        homeworkController.route(userId, courseId)
      },
    )}
  }

  def getCourse(courseId: Id[Course]): Future[Course] =
    courseRepo.find(courseId).flatMap(assertFound("course"))

  def listUserCourses(userId: Id[User]): Future[Seq[Course]] = {
    matchUserType(userId) (
      ifStudent = groupId => courseRepo.listGroupCourseIds(groupId),
      ifTeacher = courseRepo.listTeacherCourseIds(userId)
    ).flatMap(courseRepo.findCourses)
  }

  def matchUserType[T](userId: Id[User])
                      (ifStudent: Id[Group] => Future[T], ifTeacher: => Future[T]): Future[T] = {
    val groupIdOptFuture = accessRepo.findUserGroupId(userId)
    groupIdOptFuture.flatMap {
      case Some(groupId) => ifStudent(groupId)
      case None => ifTeacher
    }
  }

}
