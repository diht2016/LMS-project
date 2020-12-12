package hw.ppposd.lms.course.material

import java.sql.Timestamp
import java.util.Date

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class Material(materialId: Id[Material], courseId: Id[Course], name: String, description: String, creationDate: Timestamp)

class MaterialTable(tag: Tag) extends Table[Material](tag, "materials") {
  def materialId = column[Id[Material]]("material_id", O.PrimaryKey, O.AutoInc)
  def courseId = column[Id[Course]]("course_id")
  def name = column[String]("name")
  def description = column[String]("description")
  def creationDate = column[Timestamp]("creation_date")

  def * = (materialId, courseId, name, description, creationDate) <> (Material.tupled, Material.unapply)
}
