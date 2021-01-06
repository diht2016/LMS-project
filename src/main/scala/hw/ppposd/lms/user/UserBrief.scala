package hw.ppposd.lms.user

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Json, Writes}

case class UserBrief(id: Id[User], fullName: String)

object UserBrief extends PlayJsonSupport {
  implicit val userBriefFormat: Writes[UserBrief] = Json.writes[UserBrief]
}
