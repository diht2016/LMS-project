package hw.ppposd.lms.course.homework

import java.sql.Timestamp
import java.time.LocalDateTime

import akka.http.scaladsl.model.DateTime
import akka.http.scaladsl.server.Route
import com.fasterxml.jackson.core.JsonParseException
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.Controller
import hw.ppposd.lms.course.homework.HomeworkController.HomeworkRequest
import hw.ppposd.lms.course.{AccessRepository, Course}
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import play.api.libs.json.Json.{fromJson, toJson}
import play.api.libs.json.{Format, JsResult, JsString, JsValue, Json, Writes}

import scala.concurrent.{ExecutionContext, Future}

class HomeworkController(homeworkRepo: HomeworkRepository,
                         accessRepo: AccessRepository)
                        (implicit ec: ExecutionContext) extends Controller {

  def route(userId: Id[User], courseId: Id[Course]): Route = {
    (pathEnd & get) {
      listHomeworksOfCourse(userId, courseId)
    } ~ (pathEnd & post & patch & entity(as[HomeworkRequest])) { hw =>
        createNewHomework(userId, courseId, hw.name, hw.description, hw.startDate, hw.deadlineDate)
    } ~ (pathPrefixId[Homework] & pathEnd & delete) { homeworkId =>
      deleteHomework(userId, courseId, homeworkId)
    } ~ (pathPrefixId[Homework] & pathEnd & put & patch & entity(as[HomeworkRequest])) { (homeworkId, hw) =>
        editHomework(userId, courseId, homeworkId, hw.name, hw.description, hw.startDate, hw.deadlineDate)
    }
  }


  def listHomeworksOfCourse(userId: Id[User], courseId: Id[Course]): Future[Seq[Homework]] =
    for {
      isStudent <- accessRepo.isCourseStudent(userId, courseId)
      isTeacher <- accessRepo.isCourseTeacher(userId, courseId)
      homeworks <-
        if (isStudent){
          homeworkRepo.listOpened(courseId)
        } else if (isTeacher) {
          homeworkRepo.listAll(courseId)
        } else {
          ApiError(403, "User is not student or teacher of the course")
        }
    } yield homeworks


  def createNewHomework(userId: Id[User],
                        courseId: Id[Course],
                        name: String,
                        description: String,
                        startDate: Timestamp,
                        deadlineDate: Timestamp): Future[Id[Homework]] =
    for {
      canManage <- canManageHomeworks(userId, courseId)
      newHomeworkId <-
        if (canManage) {
          homeworkRepo.add(courseId, name, description, startDate, deadlineDate)
        } else {
          ApiError(403, "User is not a teacher of the course.")
        }
    } yield newHomeworkId


  def editHomework(userId: Id[User],
                   courseId: Id[Course],
                   homeworkId: Id[Homework],
                   name: String,
                   description: String,
                   startDate: Timestamp,
                   deadlineDate: Timestamp): Future[Homework] =
    for {
      canManage <- canManageHomeworks(userId, courseId)
      editedHomework <-
        if (canManage) {
          homeworkRepo.edit(homeworkId, name, description, startDate, deadlineDate).flatMap{
            case Some(hw) => Future.successful(hw)
            case None => ApiError(404, s"Homework with id=${homeworkId.value} doesn't exist.")
          }
        } else {
          ApiError(403, "User is not a teacher of the course.")
        }
    } yield editedHomework


  def deleteHomework(userId: Id[User], courseId: Id[Course], homeworkId: Id[Homework]): Future[Seq[Homework]] =
    for {
      canManage <- canManageHomeworks(userId, courseId)
      rest <-
        if (canManage) {
          homeworkRepo.delete(courseId, homeworkId)
        } else {
          ApiError(403, "User is not a teacher of the course")
        }
    } yield rest

  private def canManageHomeworks(userId: Id[User], courseId: Id[Course]): Future[Boolean] =
    accessRepo.isCourseTeacher(userId, courseId)

}

object HomeworkController {
  case class HomeworkRequest(name: String,
                             description: String,
                             startDate: Timestamp,
                             deadlineDate: Timestamp)

  object HomeworkRequest extends PlayJsonSupport {
    implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
      def writes(t: Timestamp): JsValue = toJson(t.toLocalDateTime)
      def reads(json: JsValue): JsResult[Timestamp] = fromJson[LocalDateTime](json).map(Timestamp.valueOf)
    }
    implicit val homeworkRequestFormat: Format[HomeworkRequest] = Json.format[HomeworkRequest]
  }
}