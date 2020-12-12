package hw.ppposd.lms.course

import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait CourseRepository {
  def create(name: String, description: String): Future[Id[Course]]
  def find(id: Id[Course]): Future[Option[Course]]
  def list(): Future[Seq[Course]]
}

class CourseRepositoryImpl extends CourseRepository {
  import hw.ppposd.lms.Schema._

  override def create(name: String, description: String): Future[Id[Course]] =
    db.run((courses returning courses.map(_.id))
      += Course(Id.auto, name, description))

  override def find(id: Id[Course]): Future[Option[Course]] =
    db.run(courses.filter(_.id === id).result.headOption)

  override def list(): Future[Seq[Course]] =
    db.run(courses.result)
}
