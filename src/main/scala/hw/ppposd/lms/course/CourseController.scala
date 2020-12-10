package hw.ppposd.lms.course

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller

import scala.concurrent.{ExecutionContext, Future}

class CourseController (courseRepo: CourseRepository) extends Controller {
  override def route(implicit ec: ExecutionContext): Route = pathSingleSlash {
    get {
      listCourses
    }
  }

  def listCourses: Future[Seq[Course]] = {
    courseRepo.list()
  }

}
