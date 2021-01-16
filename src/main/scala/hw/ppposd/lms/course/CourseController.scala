package hw.ppposd.lms.course

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.user.{User, UserCommons, UserTypeMatching}
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class CourseController(courseRepo: CourseRepository, userCommons: UserCommons, wiring: CourseWiring)
                      (implicit ec: ExecutionContext) extends Controller with UserTypeMatching {
  import wiring._
  def route(userId: Id[User]): Route = pathPrefix("courses") {
    (pathEnd & get) {
      listUserCourses(userId)
    } ~ pathPrefixId[Course] { courseId => concat(
      (pathEnd & get) {
        getCourse(courseId)
      },
      delegate(teacherController.route(userId, courseId)),
      delegate(tutorController.route(userId, courseId)),
      delegate(materialController.route(userId, courseId)),
      delegate(homeworkController.route(userId, courseId)),
    )}
  }

  private def getCourse(courseId: Id[Course]): Future[Course] =
    courseRepo.find(courseId).flatMap(assertFound("course"))

  private def listUserCourses(userId: Id[User]): Future[Seq[Course]] = {
    matchUserType(userCommons, userId) (
      ifStudent = groupId => courseRepo.listGroupCourseIds(groupId),
      ifTeacher = courseRepo.listTeacherCourseIds(userId)
    ).flatMap(courseRepo.enrichCourses)
  }
}
