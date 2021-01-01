package hw.ppposd.lms.user

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.user.personaldata.PersonalDataEntity
import hw.ppposd.lms.user.studentdata.StudentDataEntity
import play.api.libs.json.{Json, Writes}

case class UserEntity(fullName: String,
                      email: String,
                      personalData: PersonalDataEntity,
                      studentData: Option[StudentDataEntity])

object UserEntity extends PlayJsonSupport {
  implicit val userFormat: Writes[UserEntity] = Json.writes[UserEntity]
}
