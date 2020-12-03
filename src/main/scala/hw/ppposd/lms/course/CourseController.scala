package hw.ppposd.lms.course

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

case class CourseGetAll(courses: Seq[Course])
object CourseGetAll {
  implicit val courseGetAllJsonFormat = Json.format[CourseGetAll]
}


class CourseController @Inject() (val controllerComponents: ControllerComponents, courseRepo: CourseRepository)
                                 (implicit ec: ExecutionContext)
  extends BaseController {
  def getCourses = Action.async { request =>
    courseRepo.getAll().map(courses => Ok(Json.toJson(courses)))
  }

}
