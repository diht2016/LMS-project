package hw.ppposd.lms.user.personaldata

import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class PersonalData(userId: Id[User],
                        phoneNumber: Option[String],
                        city: Option[String],
                        description: Option[String],
                        vk: Option[String],
                        facebook: Option[String],
                        linkedin: Option[String],
                        instagram: Option[String])

class PersonalDataTable(tag: Tag) extends Table[PersonalData](tag, "personal_data"){
  def userId = column[Id[User]]("userId", O.PrimaryKey)
  def phoneNumber = column[Option[String]]("phoneNumber", O.Default(None))
  def city = column[Option[String]]("city", O.Default(None))
  def description = column[Option[String]]("description", O.Default(None))
  def vk = column[Option[String]]("vk", O.Default(None))
  def facebook = column[Option[String]]("facebook", O.Default(None))
  def linkedin = column[Option[String]]("linkedin", O.Default(None))
  def instagram = column[Option[String]]("instagram", O.Default(None))

  def * = (userId, phoneNumber, city, description, vk, facebook, linkedin, instagram)
   .<> (PersonalData.tupled, PersonalData.unapply)
}