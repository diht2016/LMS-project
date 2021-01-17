package hw.ppposd.lms.course.homework.solution

import java.sql.Timestamp
import java.time.LocalDateTime

import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.Controller
import hw.ppposd.lms.course.homework.solution.SolutionController.{GroupSolutionsInfo, SolutionEntity, StudentSolutionInfo}
import hw.ppposd.lms.course.homework.{Homework, HomeworkRepository}
import hw.ppposd.lms.course.{AccessRepository, Course}
import hw.ppposd.lms.group.{Group, GroupRepository}
import hw.ppposd.lms.user.{User, UserBrief, UserCommons}
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Format, Json, Writes}

import scala.concurrent.{ExecutionContext, Future}

class SolutionController(accessRepo: AccessRepository,
                         solutionRepo: SolutionRepository,
                         homeworkRepo: HomeworkRepository,
                         groupRepo: GroupRepository,
                         userCommons: UserCommons)
                        (implicit ec: ExecutionContext) extends Controller {

  def route(userId: Id[User], courseId: Id[Course], homeworkId: Id[Homework]): Route =
    pathPrefix("solutions") {
      pathEnd {
        get {
          checkViewAccess(userId, courseId)
            .accept(getSolutionInfoListForTeacher(courseId, homeworkId))
        } ~ (post & entity(as[SolutionEntity])) { sol =>
          checkUploadAccess(userId, courseId)
            .accept(uploadSolution(userId, courseId, homeworkId, sol.text))
        }
      } ~ (pathPrefixId[User] & pathEnd & get) { studentId =>
        checkViewAccess(userId, courseId)
          .accept(getSolutionText(studentId, homeworkId))
      }
    }

  private def getSolutionText(studentId: Id[User], homeworkId: Id[Homework]): Future[String] =
    solutionRepo.find(homeworkId, studentId)
      .flatMap(assertFound("solution"))
      .map(_.text)

  private def uploadSolution(userId: Id[User], courseId: Id[Course], homeworkId: Id[Homework], text: String): Future[Unit] =
    for {
      // todo: check that homework actually belongs to this course
      // todo: validate deadline and start date order in this function
      maybeHomework <- homeworkRepo.findAndCheckAvailability(homeworkId, Timestamp.valueOf(LocalDateTime.now()))
      _ <- if (maybeHomework.isEmpty) ApiError(404, s"homework with id=$homeworkId does not exists.") else Future.unit
      _ <- maybeHomework match {
        case Some((_, true)) => Future.unit
        case _ => ApiError(403, "homework is not available for solving")
      }
      solution <- solutionRepo.set(homeworkId, userId, text)
        .flatMap(assertSingleUpdate)
    } yield solution

  private def getSolutionInfoListForTeacher(courseId: Id[Course], homeworkId: Id[Homework]): Future[Seq[GroupSolutionsInfo]] = {
    def getStudentSolutionInfo(userBrief: UserBrief, homeworkId: Id[Homework]): Future[StudentSolutionInfo] =
        solutionRepo.find(homeworkId, userBrief.id).map(maybeSol => StudentSolutionInfo(userBrief, maybeSol.nonEmpty))

    def getGroupSolutionInfo(group: Group): Future[GroupSolutionsInfo] =
      groupRepo
        .listGroupStudentIds(group.id)
        .flatMap(userCommons.enrichUsers)
        .flatMap(users => Future.sequence(users.map(u => getStudentSolutionInfo(u, homeworkId))))
        .map(GroupSolutionsInfo(group, _))

    groupRepo.listCourseGroups(courseId)
      .flatMap(groups => Future.sequence(groups.map(getGroupSolutionInfo)))
  }

  private def checkViewAccess(userId: Id[User], courseId: Id[Course]): Future[Unit] =
    assertTrue(accessRepo.isCourseTeacher(userId, courseId),
      ApiError(403, "not permitted to view homework solutions"))

  private def checkUploadAccess(userId: Id[User], courseId: Id[Course]): Future[Unit] =
    assertTrue(accessRepo.isCourseStudent(userId, courseId),
      ApiError(403, "not permitted to submit homework solution"))
}

object SolutionController {
  case class StudentSolutionInfo(student: UserBrief, hasUploadSolution: Boolean)
  object StudentSolutionInfo {
    implicit val studentSolutionInfoFormat: Writes[StudentSolutionInfo] = Json.writes[StudentSolutionInfo]
  }

  case class GroupSolutionsInfo(group: Group, studentSolutions: Seq[StudentSolutionInfo])
  object GroupSolutionsInfo {
    implicit val groupSolutionsInfoFormat: Writes[GroupSolutionsInfo] = Json.writes[GroupSolutionsInfo]
  }

  case class SolutionEntity(text: String)
  object SolutionEntity extends PlayJsonSupport {
    implicit val solutionEntityFormat: Format[SolutionEntity] = Json.format[SolutionEntity]
  }
}
