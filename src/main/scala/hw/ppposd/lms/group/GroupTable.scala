package hw.ppposd.lms.group

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Json, Writes}
import slick.jdbc.H2Profile.api._

case class Group(id: Id[Group],
                 name: String,
                 department: String,
                 courseNumber: Int)
object Group extends PlayJsonSupport {
  implicit val userFormat: Writes[Group] = Json.writes[Group]
}

class GroupTable(tag: Tag) extends Table[Group](tag, "groups") {
  def id = column[Id[Group]]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def department = column[String]("department")
  def courseNumber = column[Int]("courseNumber")

  def * = (id, name, department, courseNumber) .<> ((Group.apply _).tupled, Group.unapply)
}
