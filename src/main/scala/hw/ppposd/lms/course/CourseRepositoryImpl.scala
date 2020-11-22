package hw.ppposd.lms.course
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

class CourseRepositoryImpl extends CourseRepository {
  // todo: import from config
  val db = Database.forURL("jdbc:h2:./target/db", driver="org.h2.Driver")
  val courses = TableQuery[CourseTable]

  //db.run(courses.schema.dropIfExists)
  db.run(courses.schema.createIfNotExists)

  override def create(name: String, description: String): Future[Id[Course]] = {
    db.run((courses returning courses.map(_.id)) += Course(Id.auto, name, description))
  }

  override def find(id: Id[Course]): Future[Option[Course]] = {
    db.run(courses.filter(_.id === id).result.headOption)
  }
}
