package hw.ppposd.lms.user

import hw.ppposd.lms.group.Group
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class User(id: Id[User],
                fullName: String,
                email: String,
                passwordHash: String,
                groupId: Option[Id[Group]])

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def id = column[Id[User]]("id", O.PrimaryKey, O.AutoInc)
  def fullName = column[String]("full_name")
  def email = column[String]("email")
  def passwordHash = column[String]("password_hash")
  def groupId = column[Option[Id[Group]]]("group_id")

  def * = (id, fullName, email, passwordHash, groupId) <> (User.tupled, User.unapply)
}