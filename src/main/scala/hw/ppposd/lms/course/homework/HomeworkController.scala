package hw.ppposd.lms.course.homework

import java.sql.Timestamp
import java.time.LocalDateTime

import akka.http.scaladsl.model.DateTime
import akka.http.scaladsl.server.Route
import com.fasterxml.jackson.core.JsonParseException
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.Controller
import hw.ppposd.lms.course.homework.HomeworkController.HomeworkEntity
import hw.ppposd.lms.course.{AccessRepository, Course}
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.FutureUtils.failOnFalseWith
import hw.ppposd.lms.util.Id
import play.api.libs.json.Json.{fromJson, toJson}
import play.api.libs.json.{Format, JsResult, JsString, JsValue, Json, Writes}

import scala.concurrent.{ExecutionContext, Future}

class HomeworkController(homeworkRepo: HomeworkRepository,
                         accessRepo: AccessRepository)
                        (implicit ec: ExecutionContext) extends Controller {

  def route(userId: Id[User], courseId: Id[Course]): Route = {
    pathEndOrSingleSlash {
      get {
        listCourseHomeworks(userId, courseId)
      } ~ (post & entity(as[HomeworkEntity])) { entity =>
        checkAccess(userId, courseId) { createHomework(courseId, entity) }
      }
    } ~ (pathPrefixId[Homework] & pathEnd) { materialId =>
      (put & entity(as[HomeworkEntity])) { entity =>
        checkAccess(userId, courseId) { editHomework(materialId, entity) }
      } ~ delete {
        checkAccess(userId, courseId) { deleteHomework(materialId) }
      }
    }
  }

  def listCourseHomeworks(userId: Id[User], courseId: Id[Course]): Future[Seq[Homework]] =
    accessRepo.isCourseTeacher(userId, courseId).flatMap {
      case true => homeworkRepo.listAll(courseId)
      case false => homeworkRepo.listAvailable(courseId)
    }

  def createHomework(courseId: Id[Course], entity: HomeworkEntity): Future[Id[Homework]] =
    homeworkRepo.add(courseId, entity.name, entity.description, entity.startDate, entity.deadlineDate)

  def editHomework(homeworkId: Id[Homework], entity: HomeworkEntity): Future[Unit] =
    homeworkRepo.edit(homeworkId, entity.name, entity.description, entity.startDate, entity.deadlineDate)
      .flatMap(assertSingleUpdate)

  def deleteHomework(homeworkId: Id[Homework]): Future[Unit] =
    homeworkRepo.delete(homeworkId)
      .flatMap(assertSingleUpdate)

  private def canManageHomeworks(userId: Id[User], courseId: Id[Course]): Future[Boolean] =
    accessRepo.isCourseTeacher(userId, courseId)

  private def checkAccess[T](userId: Id[User], courseId: Id[Course]): (=> Future[T]) => Future[T] =
    checkCondition(ApiError(403, "not permitted to manage homeworks")) {
      canManageHomeworks(userId, courseId)
    }
}

object HomeworkController {
  case class HomeworkEntity(name: String,
                            description: String,
                            startDate: Timestamp,
                            deadlineDate: Timestamp)

  object HomeworkEntity extends PlayJsonSupport {
    implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
      def writes(t: Timestamp): JsValue = toJson(t.toLocalDateTime)
      def reads(json: JsValue): JsResult[Timestamp] = fromJson[LocalDateTime](json).map(Timestamp.valueOf)
    }
    implicit val homeworkEntityFormat: Format[HomeworkEntity] = Json.format[HomeworkEntity]
  }
}