package hw.ppposd.lms.course.material

import java.sql.Timestamp

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Json, Writes}
import slick.jdbc.H2Profile.api._

case class Material(materialId: Id[Material], courseId: Id[Course], name: String, description: String, creationDate: Timestamp)

object Material {
  implicit val materialFormat: Writes[Material] = Json.writes[Material]
}

class MaterialTable(tag: Tag) extends Table[Material](tag, "materials") {
  def materialId = column[Id[Material]]("material_id", O.PrimaryKey, O.AutoInc)
  def courseId = column[Id[Course]]("course_id")
  def name = column[String]("name")
  def description = column[String]("description")
  def creationDate = column[Timestamp]("creation_date")

  def * = (materialId, courseId, name, description, creationDate) .<> ((Material.apply _).tupled, Material.unapply)
}
