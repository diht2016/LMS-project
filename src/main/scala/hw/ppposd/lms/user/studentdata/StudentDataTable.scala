package hw.ppposd.lms.user.studentdata

import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import StudentData._
import hw.ppposd.lms.group.Group
import slick.jdbc.H2Profile.api._

case class StudentData(userId: Id[User],
                       groupId: Id[Group],
                       yearOfEnrollment: Int,
                       degree: Degree,
                       studyForm: StudyForm,
                       learningBase: LearningBase)

object StudentData {

  type Degree = Degree.Value
  object Degree extends Enumeration {
    val Bachelor, Specialist, Master = Value
  }
  implicit val degreeMapper = MappedColumnType.base[Degree, String](_.toString, Degree.withName)

  type StudyForm = StudyForm.Value
  object StudyForm extends Enumeration {
    val Intramural, Extramural, Evening = Value
  }
  implicit val studyFormMapper = MappedColumnType.base[StudyForm, String](_.toString, StudyForm.withName)

  type LearningBase = LearningBase.Value
  object LearningBase extends Enumeration {
    val Contract, Budget = Value
  }
  implicit val learningBaseMapper = MappedColumnType.base[LearningBase, String](_.toString, LearningBase.withName)

}

class StudentDataTable(tag: Tag) extends Table[StudentData](tag, "student_data") {
  def studentId = column[Id[User]]("student_id", O.PrimaryKey)
  def groupId = column[Id[Group]]("group_id")
  def yearOfEnrollment = column[Int]("year_of_enrollment")
  def degree = column[Degree]("degree")
  def studyForm =  column[StudyForm]("study_form")
  def learningBase = column[LearningBase]("learning_base")

  def * = (studentId, groupId, yearOfEnrollment, degree, studyForm, learningBase) .<> ((StudentData.apply _).tupled, StudentData.unapply)
}