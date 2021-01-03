package hw.ppposd.lms.access

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Json, Writes}

case class CourseBrief(id: Id[Course], name: String)

object CourseBrief extends PlayJsonSupport {
  implicit val courseBriefFormat: Writes[CourseBrief] = Json.writes[CourseBrief]
}
