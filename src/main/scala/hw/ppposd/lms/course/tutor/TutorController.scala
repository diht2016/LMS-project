package hw.ppposd.lms.course.tutor

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.course.{AccessRepository, Course}
import hw.ppposd.lms.user.{User, UserBrief, UserCommons}
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class TutorController(tutorRepo: TutorRepository, accessRepo: AccessRepository, userCommons: UserCommons)
                     (implicit ec: ExecutionContext) extends Controller {
  def route(userId: Id[User], courseId: Id[Course]): Route = {
    (pathEndOrSingleSlash & get) {
      listCourseTutorBriefs(courseId)
    } ~ (pathPrefixId[User] & pathEnd & post) { otherUserId =>
      assertCanManageTutors(userId, courseId, otherUserId)
        .flatMap(_ => addTutor(courseId, otherUserId))
    } ~ (pathPrefixId[User] & pathEnd & delete) { otherUserId =>
      assertCanManageTutors(userId, courseId, otherUserId)
        .flatMap(_ => deleteTutor(courseId, otherUserId))
    }
  }

  def listCourseTutorBriefs(courseId: Id[Course]): Future[Seq[UserBrief]] =
    tutorRepo.listCourseTutorIds(courseId)
      .flatMap(userCommons.enrichUsers)

  def addTutor(courseId: Id[Course], userId: Id[User]): Future[Unit] =
    tutorRepo.add(courseId, userId)
      .flatMap(assertSingleUpdate)

  def deleteTutor(courseId: Id[Course], userId: Id[User]): Future[Unit] =
    tutorRepo.delete(courseId, userId)
      .flatMap(assertSingleUpdate)

  private def assertCanManageTutors(userId: Id[User], courseId: Id[Course], tutorId: Id[User]): Future[Unit] = {
    val userIsCourseTeacher = accessRepo.isCourseTeacher(userId, courseId)
    val tutorIsCourseStudent = accessRepo.isCourseStudent(tutorId, courseId)
    Future.unit
      .flatMap(assertTrue(userIsCourseTeacher, ApiError(403, "not permitted to manage tutors")))
      .flatMap(assertTrue(tutorIsCourseStudent, ApiError(400, "user is not a course student")))
  }
}
