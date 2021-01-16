package hw.ppposd.lms.course.homework.solution

import java.time.LocalDateTime

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.course.homework.solution.SolutionController.{GroupSolutionsInfo, StudentSolutionInfo}
import hw.ppposd.lms.course.homework.{Homework, HomeworkRepository}
import hw.ppposd.lms.course.{AccessRepository, Course}
import hw.ppposd.lms.group.{Group, GroupRepository}
import hw.ppposd.lms.user.{User, UserBrief, UserCommons}
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Json, Writes}

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
          getSolutionInfoListForTeacher(userId, courseId, homeworkId)
        } ~ (post & entity(as[String])) { text =>
          uploadSolution(userId, courseId, homeworkId, text)
        }
      } ~ (pathPrefixId[User] & pathEnd & get) { studentId =>
        getSolutionText(userId, courseId, studentId, homeworkId)
      }
    }

  private def getSolutionText(userId: Id[User], courseId: Id[Course], studentId: Id[User], homeworkId: Id[Homework]): Future[Option[String]] =
    for {
      isCourseTeacher <- accessRepo.isCourseTeacher(userId, courseId)
      maybeSolution <-
        if (isCourseTeacher) {
          solutionRepo.find(homeworkId, studentId)
        } else {
          ApiError(403, "User is not a teacher of a course")
        }
    } yield maybeSolution.map(_.text)

  private def uploadSolution(userId: Id[User], courseId: Id[Course], homeworkId: Id[Homework], text: String): Future[Solution] =
    for {
      isCourseStudent <- accessRepo.isCourseStudent(userId, courseId)
      maybeHomework <- homeworkRepo.find(homeworkId)
      _ <- if (maybeHomework.isEmpty) ApiError(404, s"Homework with id=$homeworkId does not exits.") else Future.unit
      isHomeworkOpened = maybeHomework.exists(hw => hw.deadlineDate.toLocalDateTime.isAfter(LocalDateTime.now()))
      _ <- if (!isHomeworkOpened) ApiError(403, "Homework deadline has been already passed.") else Future.unit
      solution <-
        if (isCourseStudent ) {
          solutionRepo.set(homeworkId, userId, text).flatMap{
            case Some(s) => Future.successful(s)
            case None => ApiError(500, "Failed to upload solution")
          }
        } else {
          ApiError(403, "User is not a course student")
        }
    } yield solution

  private def getSolutionInfoListForTeacher(userId: Id[User], courseId: Id[Course], homeworkId: Id[Homework]): Future[Seq[GroupSolutionsInfo]] = {
    def getStudentSolutionInfo(userBrief: UserBrief, homeworkId: Id[Homework]): Future[StudentSolutionInfo] =
        solutionRepo.find(homeworkId, userBrief.id).map(maybeSol => StudentSolutionInfo(userBrief, maybeSol.nonEmpty))

    def getGroupSolutionInfo(group: Group): Future[GroupSolutionsInfo] =
      groupRepo
        .listGroupStudentIds(group.id)
        .flatMap(userCommons.enrichUsers)
        .flatMap(users => Future.sequence(users.map(u => getStudentSolutionInfo(u, homeworkId))))
        .map(GroupSolutionsInfo(group, _))

    for {
      isCourseTeacher <- accessRepo.isCourseTeacher(userId, courseId)
      solutionInfoList <-
        if (isCourseTeacher) {
          groupRepo.listGroupsAssignedToCourse(courseId)
            .flatMap(groups => Future.sequence(groups.map(getGroupSolutionInfo)))
        } else {
          ApiError(403, "User is not a teacher of the course")
        }
    } yield solutionInfoList
  }
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
}