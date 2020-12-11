package hw.ppposd.lms.auth

import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class Session(session: String, userId: Id[User])

class SessionTable(tag: Tag) extends Table[Session](tag, "sessions") {
  def session = column[String]("session", O.PrimaryKey)
  def userId = column[Id[User]]("userId")

  def * = (session, userId) <> (Session.tupled, Session.unapply)
}
