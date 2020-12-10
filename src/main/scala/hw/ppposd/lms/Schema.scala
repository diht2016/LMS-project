package hw.ppposd.lms

import hw.ppposd.lms.course.CourseTable
import hw.ppposd.lms.group.GroupTable
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

object Schema {
  lazy val db = Database.forConfig("db")

  /**
   * Here are the tables
   */
  val courses = TableQuery[CourseTable]
  val groups = TableQuery[GroupTable]

  def createSchema(): Future[Unit] = {
    val schema = courses.schema ++ groups.schema
    val setup = DBIO.seq{schema.createIfNotExists}
    db.run(setup)
  }
}
