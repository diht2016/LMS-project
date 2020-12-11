package hw.ppposd.lms.group

import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class Group(id: Id[Group],
                 name: String,
                 department: String,
                 courseNumber: Int)

class GroupTable(tag: Tag) extends Table[Group](tag, "groups") {
  def id = column[Id[Group]]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def department = column[String]("department")
  def courseNumber = column[Int]("courseNumber")

  def * = (id, name, department, courseNumber) <> (Group.tupled, Group.unapply)
}
