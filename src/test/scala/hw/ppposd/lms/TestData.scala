package hw.ppposd.lms

import java.sql.Timestamp

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.course.homework.Homework
import hw.ppposd.lms.course.homework.solution.Solution
import hw.ppposd.lms.course.material.Material
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.User
import hw.ppposd.lms.user.personaldata.PersonalData
import hw.ppposd.lms.user.studentdata.StudentData
import hw.ppposd.lms.user.studentdata.StudentData.{Degree, LearningBase, StudyForm}
import hw.ppposd.lms.util.Id

case class TestData(
                     users: Seq[User] = Seq(),
                     courses: Seq[Course] = Seq(),
                     groups: Seq[Group] = Seq(),
                     studentData: Seq[StudentData] = Seq(),
                     personalData: Seq[PersonalData] = Seq(),
                     homeworks: Seq[Homework] = Seq(),
                     solutions: Seq[Solution] = Seq(),
                     materials: Seq[Material] = Seq()
                   )

/**
 * teacher1 -> algebraCourse
 * teacher2 -> philosophyCourse
 *
 * group1 -> (student1), algebraCourse
 * group2 -> (student2), philosophyCourse
 *
 * (courseTutor) algebraCourse -> st1
 *
 */
object TestData {
  val algebraCourse: Course = Course(Id.auto, "Linear algebra", "Some description")
  val philosophyCourse: Course = Course(Id.auto, "Philosophy", "Some description")
  val coursesData = Seq(algebraCourse, philosophyCourse)

  val student1: User = User(Id.auto, "Ivan Kozlov", "i.kozlov@lms.ru", "", None)
  val student2: User = User(Id.auto, "Daria Titova", "d.titova@lms.ru", "", None)
  val teacher1: User = User(Id.auto, "Alexey Soloviov", "a.soloviov@lms.ru", "", None)
  val teacher2: User = User(Id.auto, "Maria Gorbunova", "m.gorbunova@lms.ru", "", None)
  val usersData = Seq(student1, student2, teacher1, teacher2)

  val personalDataSt1: PersonalData = PersonalData(Id.auto, Some("89999999999"), Some("Voronezh"), None, Some("http://vk.com/ikozlov"), None, None, None)
  val personalDataSt2: PersonalData = PersonalData(Id.auto, Some("87777777777"), Some("Tomsk"), None, None, None, None, Some("link"))
  val personalDataT1: PersonalData = PersonalData(Id.auto, Some("86666666666"), Some("Kazan"), Some("Professor. Algebra course."), None, None, None, None)
  val personalDataT2: PersonalData = PersonalData(Id.auto, Some("85555555555"), Some("Yakutsk"), Some("Professor. Philosophy course."), None, None, None, None)

  val group1: Group = Group(Id.auto, "001", "Art and Science", 3)
  val group2: Group = Group(Id.auto, "002", "Biology", 2)
  val groupsData = Seq(group1, group2)

  val studentData1: StudentData = StudentData(Id.auto, Id.auto, 2018, Degree.Bachelor, StudyForm.Intramural, LearningBase.Budget)
  val studentData2: StudentData = StudentData(Id.auto, Id.auto, 2015, Degree.Master, StudyForm.Evening, LearningBase.Contract)

  val homeworkAlgebra: Homework = Homework(
    Id.auto,
    Id.auto,
    "Matrices",
    "Ex 1-10",
    Timestamp.valueOf("2021-1-1 10:00:00"),
    Timestamp.valueOf("2021-1-7 00:00:00"))
  val homeworkPhilosophy: Homework = Homework(
    Id.auto,
    Id.auto,
    "R.Dekart",
    "Make a report",
    Timestamp.valueOf("2020-12-1 10:00:00"),
    Timestamp.valueOf("2020-12-14 00:00:00"))

  val solutionAlgebraSt1: Solution = Solution(Id.auto, Id.auto, "Some text", Timestamp.valueOf("2021-1-3 12:30:00"))
  val solutionPhilosophySt2: Solution = Solution(Id.auto, Id.auto, "Some text", Timestamp.valueOf("2020-12-10 13:30:00"))

  val materialAlgebra1: Material = Material(Id.auto, Id.auto, "Matrices", "Some description", Timestamp.valueOf("2020-12-25 12:30:00"))
  val materialAlgebra2: Material = Material(Id.auto, Id.auto, "Groups", "Some description", Timestamp.valueOf("2020-12-28 12:30:00"))
  val materialPhilosophy1: Material = Material(Id.auto, Id.auto, "R.Dekart", "Some description", Timestamp.valueOf("2020-12-1 10:00:00"))
  val materialPhilosophy2: Material = Material(Id.auto, Id.auto, "Spinosa", "Some description", Timestamp.valueOf("2020-12-7 10:00:00"))
  val materialPhilosophy3: Material = Material(Id.auto, Id.auto, "I.Kant", "Some description", Timestamp.valueOf("2020-12-14 10:00:00"))
}
