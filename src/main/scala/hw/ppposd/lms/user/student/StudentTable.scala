package hw.ppposd.lms.user.student

import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import Student._
import slick.jdbc.H2Profile.api._

case class Student(userId: Id[User],
                   yearOfEnrollment: Int,
                   degree: Degree,
                   studyForm: StudyForm,
                   learningBase: LearningBase)

object Student {

  type Degree = Degree.Value
  object Degree extends Enumeration {
    val Bachelor = Value("bachelor")
    val Specialist = Value("specialist")
    val Master = Value("master")
  }
  implicit val degreeMapper = MappedColumnType.base[Degree, String](_.toString, Degree.withName)

  type StudyForm = StudyForm.Value
  object StudyForm extends Enumeration {
    val Intramural = Value("intramural")
    val Extramural = Value("extramural")
    val Evening = Value("evening")
  }
  implicit val studyFormMapper = MappedColumnType.base[StudyForm, String](_.toString, StudyForm.withName)

  type LearningBase = LearningBase.Value
  object LearningBase extends Enumeration {
    val Contract = Value("contract")
    val Budget = Value("budget")
  }
  implicit val learningBaseMapper = MappedColumnType.base[LearningBase, String](_.toString, LearningBase.withName)

}

class StudentTable(tag: Tag) extends Table[Student](tag, "students") {
  def userId = column[Id[User]]("user_id", O.PrimaryKey)
  def yearOfEnrollment = column[Int]("year_of_enrollment")
  def degree = column[Degree]("degree")
  def studyForm =  column[StudyForm]("study_form")
  def learningBase = column[LearningBase]("learning_base")

  def * = (userId, yearOfEnrollment, degree, studyForm, learningBase) <> ((Student.apply _).tupled, Student.unapply)
}