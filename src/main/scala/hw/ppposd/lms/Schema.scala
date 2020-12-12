package hw.ppposd.lms

import hw.ppposd.lms.course.CourseTable
import hw.ppposd.lms.group.GroupTable
import hw.ppposd.lms.homework.HomeworkTable
import hw.ppposd.lms.links.{CourseTeacher, CourseTeacherTable, CourseTutorTable, GroupCourseTable}
import hw.ppposd.lms.material.MaterialTable
import hw.ppposd.lms.solution.SolutionTable
import hw.ppposd.lms.user.UserTable
import hw.ppposd.lms.user.personalinfo.{PersonalInfo, PersonalInfoTable}
import hw.ppposd.lms.user.student.StudentTable
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

object Schema {
  lazy val db = Database.forConfig("db")

  /**
   * Here are the tables
   */
  val courses = TableQuery[CourseTable]
  val groups = TableQuery[GroupTable]
  val users = TableQuery[UserTable]
  val personalInfos = TableQuery[PersonalInfoTable]
  val students = TableQuery[StudentTable]
  val materials = TableQuery[MaterialTable]
  val homeworks = TableQuery[HomeworkTable]
  val solutions = TableQuery[SolutionTable]

  val courseTeacherLinks = TableQuery[CourseTeacherTable]
  val courseTutorLinks = TableQuery[CourseTutorTable]
  val groupCourseLinks = TableQuery[GroupCourseTable]

  def createSchema(): Future[Unit] = {
    val schema = courses.schema ++
      groups.schema ++
      users.schema ++
      personalInfos.schema ++
      students.schema ++
      materials.schema ++
      homeworks.schema ++
      solutions.schema ++
      courseTeacherLinks.schema ++
      courseTutorLinks.schema ++
      groupCourseLinks.schema

    val setup = DBIO.seq{schema.createIfNotExists}
    db.run(setup)
  }
}
