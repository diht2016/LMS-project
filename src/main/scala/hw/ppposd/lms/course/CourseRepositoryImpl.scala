package hw.ppposd.lms.course


import hw.ppposd.lms.util.Id
import javax.inject.{Inject, Singleton}
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

trait CourseRepository {
  /*
  def create(name: String, description: String): Future[Id]
  def find(id: Id): Future[Option[Course]]
  */
  def getAll(): Future[Seq[Course]]
}

@Singleton
class CourseRepositoryImpl @Inject() extends CourseRepository {
  import hw.ppposd.lms.Schema._

  /*
  override def create(name: String, description: String): Future[Id] = {
    db.run((courses returning courses.map(_.id)) += Course(0, name, description))
  }

  override def find(id: Id): Future[Option[Course]] = {
    db.run(courses.filter(_.id === id).result.headOption)
  }
*/
  override def getAll(): Future[Seq[Course]] = {
    db.run(courses.result)
  }
}
