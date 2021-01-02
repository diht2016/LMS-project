package hw.ppposd.lms.auth

import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class Verification(code: String, userId: Id[User])

class VerificationTable(tag: Tag) extends Table[Verification](tag, "verifications") {
  def code = column[String]("code", O.PrimaryKey)
  def userId = column[Id[User]]("userId")

  def * = (code, userId) .<> (Verification.tupled, Verification.unapply)
}
