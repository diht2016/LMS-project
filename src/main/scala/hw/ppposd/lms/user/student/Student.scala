package hw.ppposd.lms.user.student

import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import Student._

case class Student(userId: Id[User],
                   groupId: Id[Group],
                   yearOfEnrollment: Int,
                   degree: Degree,
                   studyForm: StudyForm,
                   learningBase: LearningBase)

object Student {
  type Degree = Degree.Value
  object Degree extends Enumeration {
    val Bachelor, Specialist, Master = Value
  }
  type StudyForm = StudyForm.Value
  object StudyForm extends Enumeration {
    val Intramural, Extramural, Evening = Value
  }
  type LearningBase = LearningBase.Value
  object LearningBase extends Enumeration {
    val Contract, Budget = Value
  }
}
