package hw.ppposd.lms.user.personaldata

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import play.api.libs.json.{Format, Json}

case class PersonalDataEntity(phoneNumber: Option[String],
                              city: Option[String],
                              description: Option[String],
                              vk: Option[String],
                              facebook: Option[String],
                              linkedin: Option[String],
                              instagram: Option[String])

object PersonalDataEntity extends PlayJsonSupport {
  implicit val personalDataEntityFormat: Format[PersonalDataEntity] = Json.format[PersonalDataEntity]

  private val checkList = List[PersonalDataEntity => Option[String]](
    validate(_.phoneNumber, isPhoneValid, "invalid phone number"),
    validate(_.vk, isLinkValid("vk.com"), "invalid vk link"),
    validate(_.facebook, isLinkValid("facebook.com"), "invalid facebook link"),
    validate(_.linkedin, isLinkValid("linkedin.com"), "invalid linkedin link"),
    validate(_.instagram, isLinkValid("instagram.com"), "invalid instagram link"),
  )

  def validateFields(entity: PersonalDataEntity): Option[String] =
    checkList.map(_(entity)).find(_.nonEmpty).flatten

  def validate(field: PersonalDataEntity => Option[String],
               checker: String => Boolean,
               errorMessage: String)(entity: PersonalDataEntity): Option[String] =
    field(entity).flatMap(value => Option.unless(checker(value))(errorMessage))

  def isPhoneValid(phone: String): Boolean =
    phone.matches("\\+7\\d{10}")

  def isLinkValid(domain: String)(link: String): Boolean =
    link.startsWith(s"https://$domain/")
}
