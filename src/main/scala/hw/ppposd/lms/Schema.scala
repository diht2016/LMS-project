package hw.ppposd.lms

import hw.ppposd.lms.auth.{SessionTable, VerificationTable}
import hw.ppposd.lms.course.CourseTable
import hw.ppposd.lms.group.{GroupCourseTable, GroupTable}
import hw.ppposd.lms.course.homework.HomeworkTable
import hw.ppposd.lms.course.material.MaterialTable
import hw.ppposd.lms.course.homework.solution.SolutionTable
import hw.ppposd.lms.course.teacher.CourseTeacherTable
import hw.ppposd.lms.course.tutor.CourseTutorTable
import hw.ppposd.lms.user.UserTable
import hw.ppposd.lms.user.personaldata.PersonalDataTable
import hw.ppposd.lms.user.studentdata.StudentDataTable
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

object Schema {
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

  val tables = List(
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

  def createSchema(implicit db: Database): Future[Unit] = {
    val schema = tables.map(_.schema).reduce(_ ++ _)

    val setup = DBIO.seq{schema.createIfNotExists}
    db.run(setup)
  }
}
