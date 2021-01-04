package hw.ppposd.lms

import hw.ppposd.lms.access._
import hw.ppposd.lms.auth.{SessionTable, VerificationTable}
import hw.ppposd.lms.course.CourseTable
import hw.ppposd.lms.course.homework.HomeworkTable
import hw.ppposd.lms.course.homework.solution.SolutionTable
import hw.ppposd.lms.course.material.MaterialTable
import hw.ppposd.lms.group.GroupTable
import hw.ppposd.lms.user.UserTable
import hw.ppposd.lms.user.personaldata.PersonalDataTable
import hw.ppposd.lms.user.studentdata.StudentDataTable
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{Await, Future}

object SpecSchema {
  implicit val db = Database.forConfig("testdb")
  val timeout = 5.seconds

  var testData: TestData = TestData()

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

  def dropDb(): Future[List[Int]] = db.run(deleteTables)
  def setupDb(): Future[Unit] = db.run(createSchema)

  private def deleteTables = {
    val deleteActions = for {
      table <- schema
    } yield table.delete

    DBIO.sequence(deleteActions)
  }

  private def createSchema = {
    schema.map(_.schema).reduce(_ ++ _).createIfNotExists
  }

  def fillDb: Unit = {
    import TestData._

    val usersInserted = insertAndGetRows(usersData, users)
    val coursesInserted = insertAndGetRows(coursesData, courses)
    val groupsInserted = insertAndGetRows(groupsData, groups)

    val studentDataFull = Seq(
      studentData1.copy(userId = usersInserted(0).id, groupId = groupsInserted(0).id),
      studentData2.copy(userId = usersInserted(1).id, groupId = groupsInserted(1).id))
    val studentDataInserted = insertAndGetRows(studentDataFull, studentData)

    val personalDataFull = Seq(
      personalDataSt1.copy(userId = usersInserted(0).id),
      personalDataSt2.copy(userId = usersInserted(1).id),
      personalDataT1.copy(userId = usersInserted(2).id),
      personalDataT2.copy(userId = usersInserted(3).id)
    )
    val personalDataInserted = insertAndGetRows(personalDataFull, personalData)

    val homeworksFull = Seq(
      homeworkAlgebra.copy(courseId = coursesInserted(0).id),
      homeworkPhilosophy.copy(courseId = coursesInserted(1).id)
    )
    val homeworksInserted = insertAndGetRows(homeworksFull, homeworks)

    val materialsFull = Seq(
      materialAlgebra1.copy(courseId = coursesInserted(0).id),
      materialAlgebra2.copy(courseId = coursesInserted(0).id),
      materialPhilosophy1.copy(courseId = coursesInserted(1).id),
      materialPhilosophy2.copy(courseId = coursesInserted(1).id),
      materialPhilosophy3.copy(courseId = coursesInserted(1).id)
    )
    val materialsInserted = insertAndGetRows(materialsFull, materials)

    val solutionsFull = Seq(
      solutionAlgebraSt1.copy(studentId = usersInserted(0).id, homeworkId = homeworksInserted(0).homeworkId),
      solutionPhilosophySt2.copy(studentId = usersInserted(1).id, homeworkId = homeworksInserted(1).homeworkId)
    )
    val solutionsInserted = insertAndGetRows(solutionsFull, solutions)

    val courseTeacherMap = Seq(
      CourseTeacher(coursesInserted(0).id, usersInserted(2).id),
      CourseTeacher(coursesInserted(1).id, usersInserted(3).id)
    )
    insertAndGetRows(courseTeacherMap, courseTeacherLinks)

    val courseTutorMap = Seq(CourseTutor(coursesInserted(0).id, usersInserted(0).id))
    insertAndGetRows(courseTutorMap, courseTutorLinks)

    val groupCourseMap = Seq(
      GroupCourse(groupsInserted(0).id, coursesInserted(0).id),
      GroupCourse(groupsInserted(1).id, coursesInserted(1).id),
    )
    insertAndGetRows(groupCourseMap, groupCourseLinks)

    //MUTATIONS!
    testData = TestData(
      usersInserted,
      coursesInserted,
      groupsInserted,
      studentDataInserted,
      personalDataInserted,
      homeworksInserted,
      solutionsInserted,
      materialsInserted)
  }

  private def insertAndGetRows[T, E <: Table[T]](rows: Seq[T], tableQuery: TableQuery[E]) = {
    val insert = for {
      row <- rows
    } yield (tableQuery returning tableQuery) += row
    Await.result(db.run(DBIO.sequence(insert)), timeout)
  }
}