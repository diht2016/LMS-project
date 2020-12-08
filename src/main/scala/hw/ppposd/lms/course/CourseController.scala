package hw.ppposd.lms.course

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

class CourseController @Inject() (val controllerComponents: ControllerComponents, courseRepo: CourseRepository)
                                 (implicit ec: ExecutionContext) extends BaseController {
  def listCourses = Action.async {
    println("1")
    val l = courseRepo.list()
    println("2")
    l.map(
      courses => Ok(
        Json.toJson(courses)
        //"asd" + courses.head.name
      )
    )
  }

}
