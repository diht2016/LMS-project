package hw.ppposd.lms.user.studentdata

import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import StudentData._
import slick.jdbc.H2Profile.api._

case class StudentData(userId: Id[User],
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
  def userId = column[Id[User]]("user_id", O.PrimaryKey)
  def yearOfEnrollment = column[Int]("year_of_enrollment")
  def degree = column[Degree]("degree")
  def studyForm =  column[StudyForm]("study_form")
  def learningBase = column[LearningBase]("learning_base")

  def * = (userId, yearOfEnrollment, degree, studyForm, learningBase) <> ((StudentData.apply _).tupled, StudentData.unapply)
}