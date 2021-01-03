package hw.ppposd.lms.course

import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future
import hw.ppposd.lms.Schema._

trait CourseRepository {
  def create(name: String, description: String): Future[Id[Course]]
  def find(id: Id[Course]): Future[Option[Course]]
  def list(): Future[Seq[Course]]
}

class CourseRepositoryImpl(implicit database: Database) extends CourseRepository {

  override def create(name: String, description: String): Future[Id[Course]] =
    database.run((courses returning courses.map(_.id))
      += Course(Id.auto, name, description))

  override def find(id: Id[Course]): Future[Option[Course]] =
    database.run(courses.filter(_.id === id).result.headOption)

  override def list(): Future[Seq[Course]] =
    database.run(courses.result)
}
