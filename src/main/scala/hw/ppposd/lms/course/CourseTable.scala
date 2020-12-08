package hw.ppposd.lms.course

import hw.ppposd.lms.util.Id
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

case class Course(id: Id[Course], name: String, description: String)
object Course {
  implicit val courseJsonFormat = Json.format[Course]
}

class CourseTable(tag: Tag) extends Table[Course](tag, "courses") {
  def id = column[Id[Course]]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def description = column[String]("description")

  def * = (id, name, description) <> ((Course.apply _).tupled, Course.unapply)
}
