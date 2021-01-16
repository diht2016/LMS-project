package hw.ppposd.lms.auth

import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class Session(token: String, userId: Id[User])

class SessionTable(tag: Tag) extends Table[Session](tag, "sessions") {
  def token = column[String]("token", O.PrimaryKey)
  def userId = column[Id[User]]("userId")

  def * = (token, userId) .<> (Session.tupled, Session.unapply)
}
