package hw.ppposd.lms

import hw.ppposd.lms.course.CourseTable
import hw.ppposd.lms.group.GroupTable
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.H2Profile.api._

object Schema {
  lazy val db = Database.forConfig("db")

  /**
   * Here are the tables
   */
  val courses = TableQuery[CourseTable]
  val groups = TableQuery[GroupTable]

  def createSchema() = {
    val schema = courses.schema ++ groups.schema
    val setup = DBIO.seq{schema.create}
    db.run(setup)
  }

}
