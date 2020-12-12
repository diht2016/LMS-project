package hw.ppposd.lms

import hw.ppposd.lms.auth.{SessionTable, VerificationTable}
import hw.ppposd.lms.course.CourseTable
import hw.ppposd.lms.group.GroupTable
import hw.ppposd.lms.course.homework.HomeworkTable
import hw.ppposd.lms.links.{CourseTeacher, CourseTeacherTable, CourseTutorTable, GroupCourseTable}
import hw.ppposd.lms.course.material.MaterialTable
import hw.ppposd.lms.course.homework.solution.SolutionTable
import hw.ppposd.lms.user.UserTable
import hw.ppposd.lms.user.personaldata.{PersonalData, PersonalDataTable}
import hw.ppposd.lms.user.studentdata.StudentDataTable
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

object Schema {
  lazy val db = Database.forConfig("db")

  /**
   * Here are the tables
   */
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

  def createSchema(): Future[Unit] = {
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
    ).map(_.schema).reduce(_ ++ _)

    val setup = DBIO.seq{schema.createIfNotExists}
    db.run(setup)
  }
}
