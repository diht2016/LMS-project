package hw.ppposd.lms.course.homework

import java.sql.Timestamp
import java.time.LocalDateTime

import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.Controller
import hw.ppposd.lms.course.homework.HomeworkController.HomeworkEntity
import hw.ppposd.lms.course.{AccessRepository, Course}
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Format, Json}

import scala.concurrent.{ExecutionContext, Future}

class HomeworkController(homeworkRepo: HomeworkRepository,
                         accessRepo: AccessRepository,
                         wiring: HomeworkWiring)
                        (implicit ec: ExecutionContext) extends Controller {
  import wiring._
  def route(userId: Id[User], courseId: Id[Course]): Route = pathPrefix("homeworks") {
    pathEnd {
      get {
        listCourseHomeworks(userId, courseId)
      } ~ (post & entity(as[HomeworkEntity])) { entity =>
        checkAccess(userId, courseId) accept createHomework(courseId, entity)
      }
    } ~ pathPrefixId[Homework] { homeworkId =>
      pathEnd {
        (put & entity(as[HomeworkEntity])) { entity =>
          checkAccess(userId, courseId) accept editHomework(homeworkId, entity)
        } ~ delete {
          checkAccess(userId, courseId) accept deleteHomework(homeworkId)
        }
      } ~ delegate(solutionController.route(userId, courseId, homeworkId))
    }
  }

  private def listCourseHomeworks(userId: Id[User], courseId: Id[Course]): Future[Seq[Homework]] =
    accessRepo.isCourseTeacher(userId, courseId).flatMap {
      case true => homeworkRepo.list(courseId)
      case false => homeworkRepo.listStarted(courseId, Timestamp.valueOf(LocalDateTime.now))
    }

  private def createHomework(courseId: Id[Course], entity: HomeworkEntity): Future[Id[Homework]] =
    homeworkRepo.add(courseId, entity.name, entity.description, entity.startDate, entity.deadlineDate)

  private def editHomework(homeworkId: Id[Homework], entity: HomeworkEntity): Future[Unit] =
    homeworkRepo.edit(homeworkId, entity.name, entity.description, entity.startDate, entity.deadlineDate)
      .flatMap(assertSingleUpdate)

  private def deleteHomework(homeworkId: Id[Homework]): Future[Unit] =
    homeworkRepo.delete(homeworkId)
      .flatMap(assertSingleUpdate)

  private def checkAccess(userId: Id[User], courseId: Id[Course]): Future[Unit] =
    assertTrue(accessRepo.isCourseTeacher(userId, courseId),
      ApiError(403, "not permitted to manage homeworks"))
}

object HomeworkController {
  case class HomeworkEntity(name: String,
                            description: String,
                            startDate: Timestamp,
                            deadlineDate: Timestamp)

  object HomeworkEntity extends PlayJsonSupport {
    import hw.ppposd.lms.util.JsonUtils.timestampFormat

    implicit val homeworkEntityFormat: Format[HomeworkEntity] = Json.format[HomeworkEntity]
  }
}
