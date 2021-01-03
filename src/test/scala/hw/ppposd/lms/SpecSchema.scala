package hw.ppposd.lms

import hw.ppposd.lms.access.{CourseTeacherTable, CourseTutorTable, GroupCourseTable}
import hw.ppposd.lms.auth.{SessionTable, VerificationTable}
import hw.ppposd.lms.course.{Course, CourseTable}
import hw.ppposd.lms.course.homework.HomeworkTable
import hw.ppposd.lms.course.homework.solution.SolutionTable
import hw.ppposd.lms.course.material.MaterialTable
import hw.ppposd.lms.group.GroupTable
import hw.ppposd.lms.user.{User, UserTable}
import hw.ppposd.lms.user.personaldata.PersonalDataTable
import hw.ppposd.lms.user.studentdata.StudentDataTable
import hw.ppposd.lms.util.Id
import slick.dbio.{DBIO, _}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

object SpecSchema {
  implicit val db = Database.forConfig("testdb")

  val sessions = TableQuery[SessionTable]
  val verifications = TableQuery[VerificationTable]
  val courses = TableQuery[CourseTable]
  val materials = TableQuery[MaterialTable]
  val homeworks = TableQuery[HomeworkTable]
  val solutions = TableQuery[SolutionTable]
  val groups = TableQuery[GroupTable]
  val users = TableQuery[UserTable]
  val personalData = TableQuery[PersonalDataTable]
  val studentData = TableQuery[StudentDataTable]

  val courseTeacherLinks = TableQuery[CourseTeacherTable]
  val courseTutorLinks = TableQuery[CourseTutorTable]
  val groupCourseLinks = TableQuery[GroupCourseTable]

  val schema = List(
    sessions,
    verifications,
    courses,
    materials,
    homeworks,
    solutions,
    groups,
    users,
    personalData,
    studentData,
    courseTeacherLinks,
    courseTutorLinks,
    groupCourseLinks,
  )

  val algebraCourse = Course(Id.auto, "Linear algebra", "Some description")
  val philosophyCourse = Course(Id.auto, "Philosophy", "Some description")
  val coursesData = Seq(algebraCourse, philosophyCourse)

  val user1 = User(Id.auto, "Ivan Kozlov", "i.kozlov@lms.ru", "", None)
  val usersData = Seq(user1)

  def dropDb(): Future[List[Int]] = db.run(deleteTables)
  def setupDb(): Future[Unit] = db.run(DBIO.seq(createSchema, insertData))

  private def deleteTables = {
    val deleteActions = for {
      table <- schema
    } yield table.delete

    DBIO.sequence(deleteActions)
  }

  private def createSchema = {
    schema.map(_.schema).reduce(_ ++ _).createIfNotExists
  }

  private def insertData = (courses ++= coursesData) >>
      (users ++= usersData)
}
