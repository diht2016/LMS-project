package hw.ppposd.lms.auth

import slick.jdbc.H2Profile.api._

case class Verification(code: String, fullName: String)

class VerificationTable(tag: Tag) extends Table[Verification](tag, "verifications") {
  def code = column[String]("code")
  def fullName = column[String]("fullName")

  def * = (code, fullName) <> (Verification.tupled, Verification.unapply)
}
