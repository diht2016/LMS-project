package hw.ppposd.lms.course

import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

class CourseTable(tag: Tag) extends Table[Course](tag, "courses") {
  def id = column[Id[Course]]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def description = column[String]("description")

  def * = (id, name, description) <> (Course.tupled, Course.unapply)
}
