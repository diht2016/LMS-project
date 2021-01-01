package hw.ppposd.lms.user.studentdata

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.studentdata.StudentData._
import play.api.libs.json.{Json, Writes}

case class StudentDataEntity(group: Group,
                             yearOfEnrollment: Int,
                             degree: Degree,
                             studyForm: StudyForm,
                             learningBase: Option[LearningBase])

object StudentDataEntity extends PlayJsonSupport {
  implicit val userFormat: Writes[StudentDataEntity] = Json.writes[StudentDataEntity]
}
