package hw.ppposd.lms.course

import hw.ppposd.lms.util.Id

import scala.concurrent.Future

trait CourseRepository {
  def create(name: String, description: String): Future[Id[Course]]
  def find(id: Id[Course]): Future[Option[Course]]
}
