package hw.ppposd.lms.course.teacher

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.course.{AccessRepository, Course}
import hw.ppposd.lms.user.{User, UserBrief, UserCommons}
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class TeacherController(teacherRepo: TeacherRepository, userCommons: UserCommons)
                       (implicit ec: ExecutionContext) extends Controller {
  def route(userId: Id[User], courseId: Id[Course]): Route = {
    (pathEnd & get) {
      listCourseTutorBriefs(courseId)
    }
  }

  def listCourseTutorBriefs(courseId: Id[Course]): Future[Seq[UserBrief]] =
    teacherRepo.listCourseTutorIds(courseId)
      .flatMap(userCommons.enrichUsers)
}
