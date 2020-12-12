package hw.ppposd.lms.user.personalinfo

import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class PersonalInfo(userId: Id[User],
                        phoneNumber: Option[String],
                        city: Option[String],
                        description: Option[String],
                        vk: Option[String],
                        facebook: Option[String],
                        linkedin: Option[String],
                        instagram: Option[String])

object PersonalInfo {
  val empty: PersonalInfo = PersonalInfo(Id.auto, None, None, None, None, None, None, None)
}

class PersonalInfoTable(tag: Tag) extends Table[PersonalInfo](tag, "personal_info"){
  def userId = column[Id[User]]("userId", O.PrimaryKey)
  def phoneNumber = column[Option[String]]("phoneNumber", O.Default(None))
  def city = column[Option[String]]("city")
  def description = column[Option[String]]("description")
  def vk = column[Option[String]]("vk")
  def facebook = column[Option[String]]("facebook")
  def linkedin = column[Option[String]]("linkedin")
  def instagram = column[Option[String]]("instagram")

  def * = (userId, phoneNumber, city, description, vk, facebook, linkedin, instagram) <>
    ((PersonalInfo.apply _).tupled, PersonalInfo.unapply)
}