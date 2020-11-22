package hw.ppposd.lms.user.personalinfo

import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

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