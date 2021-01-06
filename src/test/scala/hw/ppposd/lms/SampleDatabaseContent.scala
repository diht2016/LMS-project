package hw.ppposd.lms

import java.sql.Timestamp

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.course.homework.Homework
import hw.ppposd.lms.course.homework.solution.Solution
import hw.ppposd.lms.course.material.Material
import hw.ppposd.lms.course.teacher.CourseTeacher
import hw.ppposd.lms.course.tutor.CourseTutor
import hw.ppposd.lms.group.{Group, GroupCourse}
import hw.ppposd.lms.user.User
import hw.ppposd.lms.user.personaldata.PersonalData
import hw.ppposd.lms.user.studentdata.StudentData
import hw.ppposd.lms.user.studentdata.StudentData.{Degree, LearningBase, StudyForm}
import hw.ppposd.lms.util.Id
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.H2Profile.api._

import scala.concurrent.duration.DurationInt
import scala.concurrent.Await

trait SampleDatabaseContent {
  val algebraCourse: Course = Course(Id.auto, "Linear algebra", "Some description")
  val philosophyCourse: Course = Course(Id.auto, "Philosophy", "Some description")
  val coursesData = Seq(algebraCourse, philosophyCourse)

  val student1: User = User(Id.auto, "Ivan Kozlov", "i.kozlov@lms.ru", "", None)
  val student2: User = User(Id.auto, "Daria Titova", "d.titova@lms.ru", "", None)
  val teacher1: User = User(Id.auto, "Alexey Soloviov", "a.soloviov@lms.ru", "", None)
  val teacher2: User = User(Id.auto, "Maria Gorbunova", "m.gorbunova@lms.ru", "", None)

  val personalDataSt1: PersonalData = PersonalData(Id.auto, Some("89999999999"), Some("Voronezh"), None, Some("http://vk.com/ikozlov"), None, None, None)
  val personalDataSt2: PersonalData = PersonalData(Id.auto, Some("87777777777"), Some("Tomsk"), None, None, None, None, Some("link"))
  val personalDataT1: PersonalData = PersonalData(Id.auto, Some("86666666666"), Some("Kazan"), Some("Professor. Algebra course."), None, None, None, None)
  val personalDataT2: PersonalData = PersonalData(Id.auto, Some("85555555555"), Some("Yakutsk"), Some("Professor. Philosophy course."), None, None, None, None)

  val group1: Group = Group(Id.auto, "001", "Art and Science", 3)
  val group2: Group = Group(Id.auto, "002", "Biology", 2)
  val groupsData = Seq(group1, group2)

  val studentData1: StudentData = StudentData(Id.auto, 2018, Degree.Bachelor, StudyForm.Intramural, LearningBase.Budget)
  val studentData2: StudentData = StudentData(Id.auto, 2015, Degree.Master, StudyForm.Evening, LearningBase.Contract)

  val homeworkAlgebra1: Homework = Homework(
    Id.auto,
    Id.auto,
    "Matrices",
    "Ex 1-10",
    Timestamp.valueOf("2021-1-1 10:00:00"),
    Timestamp.valueOf("2021-1-7 00:00:00"))

  val homeworkAlgebra2: Homework = Homework(
    Id.auto,
    Id.auto,
    "Groups",
    "Ex 1-10",
    Timestamp.valueOf("2020-12-1 10:00:00"),
    Timestamp.valueOf("2020-12-7 00:00:00"))
  val homeworkAlgebra3: Homework = Homework(
    Id.auto,
    Id.auto,
    "Rotations",
    "Ex 1-10",
    Timestamp.valueOf("2020-12-8 10:00:00"),
    Timestamp.valueOf("2020-12-14 00:00:00"))
  val homeworkPhilosophy1: Homework = Homework(
    Id.auto,
    Id.auto,
    "R.Descartes",
    "Make a report",
    Timestamp.valueOf("2021-2-1 10:00:00"),
    Timestamp.valueOf("2021-2-7 00:00:00"))
  val homeworkPhilosophy2: Homework = Homework(
    Id.auto,
    Id.auto,
    "Spinoza",
    "Make a report",
    Timestamp.valueOf("2021-2-8 10:00:00"),
    Timestamp.valueOf("2021-2-14 00:00:00"))
  val homeworkPhilosophy3: Homework = Homework(
    Id.auto,
    Id.auto,
    "I.Kant",
    "Make a report",
    Timestamp.valueOf("2020-12-14 10:00:00"),
    Timestamp.valueOf("2020-12-11 00:00:00"))

  val solutionAlgebraSt1: Solution = Solution(Id.auto, Id.auto, "Some text", Timestamp.valueOf("2021-1-3 12:30:00"))
  val solutionPhilosophySt2: Solution = Solution(Id.auto, Id.auto, "Some text", Timestamp.valueOf("2020-12-10 13:30:00"))

  val materialAlgebra1: Material = Material(Id.auto, Id.auto, "Matrices", "Some description", Timestamp.valueOf("2020-12-25 12:30:00"))
  val materialAlgebra2: Material = Material(Id.auto, Id.auto, "Groups", "Some description", Timestamp.valueOf("2020-12-28 12:30:00"))
  val materialPhilosophy1: Material = Material(Id.auto, Id.auto, "R.Descartes", "Some description", Timestamp.valueOf("2020-12-1 10:00:00"))
  val materialPhilosophy2: Material = Material(Id.auto, Id.auto, "Spinoza", "Some description", Timestamp.valueOf("2020-12-7 10:00:00"))
  val materialPhilosophy3: Material = Material(Id.auto, Id.auto, "I.Kant", "Some description", Timestamp.valueOf("2020-12-14 10:00:00"))

}

object SampleDatabaseContent extends SampleDatabaseContent {
  import Schema._

  def fillDatabase(implicit db: Database): TestData = {

    val coursesFull = insertAndReturnAll(coursesData, courses)
    val groupsFull = insertAndReturnAll(groupsData, groups)
    val usersPartiallyFull = Seq(
      student1.copy(groupId = Some(groupsFull(0).id)),
      student2.copy(groupId = Some(groupsFull(1).id)),
      teacher1,
      teacher2,
    )
    val usersFull = insertAndReturnAll(usersPartiallyFull, users)

    val studentDataFull = Seq(
      studentData1.copy(userId = usersFull(0).id),
      studentData2.copy(userId = usersFull(1).id))
    insertRows(studentDataFull, studentData)

    val personalDataFull = Seq(
      personalDataSt1.copy(userId = usersFull(0).id),
      personalDataSt2.copy(userId = usersFull(1).id),
      personalDataT1.copy(userId = usersFull(2).id),
      personalDataT2.copy(userId = usersFull(3).id)
    )
    insertRows(personalDataFull, personalData)

    val homeworksAlmostFull = Seq(
      homeworkAlgebra1.copy(courseId = coursesFull(0).id),
      homeworkAlgebra2.copy(courseId = coursesFull(0).id),
      homeworkAlgebra3.copy(courseId = coursesFull(0).id),
      homeworkPhilosophy1.copy(courseId = coursesFull(1).id),
      homeworkPhilosophy2.copy(courseId = coursesFull(1).id),
      homeworkPhilosophy3.copy(courseId = coursesFull(1).id)
    )

    val homeworksFull = insertAndReturnAll(homeworksAlmostFull, homeworks)

    val materialsFull = Seq(
      materialAlgebra1.copy(courseId = coursesFull(0).id),
      materialAlgebra2.copy(courseId = coursesFull(0).id),
      materialPhilosophy1.copy(courseId = coursesFull(1).id),
      materialPhilosophy2.copy(courseId = coursesFull(1).id),
      materialPhilosophy3.copy(courseId = coursesFull(1).id)
    )
    insertRows(materialsFull, materials)

    val solutionsFull = Seq(
      solutionAlgebraSt1.copy(studentId = usersFull(0).id, homeworkId = homeworksFull(0).homeworkId),
      solutionPhilosophySt2.copy(studentId = usersFull(1).id, homeworkId = homeworksFull(1).homeworkId)
    )
    insertRows(solutionsFull, solutions)

    val courseTeacherMap = Seq(
      CourseTeacher(coursesFull(0).id, usersFull(2).id),
      CourseTeacher(coursesFull(1).id, usersFull(3).id)
    )
    insertRows(courseTeacherMap, courseTeacherLinks)

    val courseTutorMap = Seq(CourseTutor(coursesFull(0).id, usersFull(0).id))
    insertRows(courseTutorMap, courseTutorLinks)

    val groupCourseMap = Seq(
      GroupCourse(groupsFull(0).id, coursesFull(0).id),
      GroupCourse(groupsFull(1).id, coursesFull(1).id),
    )
    insertRows(groupCourseMap, groupCourseLinks)

    TestData(
      usersFull,
      coursesFull,
      groupsFull,
      studentDataFull,
      personalDataFull,
      homeworksFull,
      solutionsFull,
      materialsFull)
  }

  private def insertAndReturnAll[T, Q <: Table[T]]
    (rows: Seq[T], tableQuery: TableQuery[Q])(implicit db: Database): Seq[T] = {
    val action = (tableQuery ++= rows).andThen(tableQuery.result)
    Await.result(db.run(action), 3.seconds)
  }

  private def insertRows[T, E <: Table[T]](rows: Seq[T], tableQuery: TableQuery[E])(implicit db: Database): Unit = {
    Await.result(db.run(DBIO.seq(tableQuery ++= rows)), 3.seconds)
  }
}
