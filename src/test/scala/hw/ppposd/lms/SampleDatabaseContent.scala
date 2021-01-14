package hw.ppposd.lms

import java.sql.Timestamp

import hw.ppposd.lms.auth.{AuthUtils, Verification}
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

object SampleDatabaseContent {
  val course1: Course = Course(id(31), "Algebra", "Description1")
  val course2: Course = Course(id(32), "Philosophy", "Description2")
  val course3: Course = Course(id(33), "Physics", "Description3")

  val group1: Group = Group(id(21), "001", "Art and Science", 6)
  val group2: Group = Group(id(22), "002", "Biology", 5)
  val group3: Group = Group(id(23), "003", "Physics", 1)

  val student1: User = User(id(1), "test-student1", "student1@lms.ru", AuthUtils.hashPassword("test-student1"), Some(id(21)))
  val student2: User = User(id(2), "test-student2", "student2@lms.ru", AuthUtils.hashPassword("test-student2"), Some(id(21)))
  val student3: User = User(id(3), "test-student3", "student3@lms.ru", AuthUtils.hashPassword("test-student3"), Some(id(21)))
  val student4: User = User(id(4), "test-student4", "student4@lms.ru", AuthUtils.hashPassword("test-student4"), Some(id(22)))
  val student5: User = User(id(5), "test-student5", "student5@lms.ru", AuthUtils.hashPassword("test-student5"), Some(id(22)))
  val student6: User = User(id(6), "test-student6", "student6@lms.ru", AuthUtils.hashPassword("test-student6"), Some(id(22)))
  val student7: User = User(id(7), "test-student7", "student7@lms.ru", AuthUtils.hashPassword("test-student7"), Some(id(23)))
  val student8: User = User(id(8), "test-student8", "student8@lms.ru", AuthUtils.hashPassword("test-student8"), Some(id(23)))
  val student9: User = User(id(9), "test-student9", "student9@lms.ru", AuthUtils.hashPassword("test-student9"), Some(id(23)))

  val teacher1: User = User(id(11), "test-teacher1", "teacher1@lms.ru", AuthUtils.hashPassword("test-teacher1"), None)
  val teacher2: User = User(id(12), "test-teacher2", "teacher2lms.ru", AuthUtils.hashPassword("test-teacher2"), None)
  val teacher3: User = User(id(13), "test-teacher3", "teacher3@lms.ru", AuthUtils.hashPassword("test-teacher3"), None)
  val teacherUnregistered: User = User(id(14), "test-teacher3", "", "", None)
  val verificationT: Verification = Verification("sample-code", id(14))

  val personalDataS1: PersonalData = PersonalData(id(1), Some("+79990000001"), Some("Voronezh"), Some("Student"), Some("https://vk.com/stu1"), None, None, None)
  val personalDataS2: PersonalData = PersonalData(id(2), Some("+79990000002"), Some("Tomsk"), Some("Student"), None, None, None, Some("https://instagram/stu2"))
  val personalDataS3: PersonalData = PersonalData(id(3), Some("+79990000003"), Some("Moscow"), Some("Student"), Some("https://vk.com/stu3"), None, None, None)
  val personalDataS4: PersonalData = PersonalData(id(4), Some("+79990000004"), Some("Tver"), Some("Student"), None, Some("https://facebook.com/stu4"), None, None)
  val personalDataS5: PersonalData = PersonalData(id(5), Some("+79990000005"), Some("Ufa"), Some("Student"), None, None, Some("https://linkedin.com/stu5"), None)
  val personalDataS6: PersonalData = PersonalData(id(6), Some("+79990000006"), Some("Yakutsk"), Some("Student"), Some("https://vk.com/stu6"), None, Some("https://linkedin.com/stu6"), None)
  val personalDataS7: PersonalData = PersonalData(id(7), Some("+79990000007"), Some("Novokuznetzk"), Some("Student"), None, Some("https://facebook.com/stu7"), Some("https://linkedin.com/stu7"), None)
  val personalDataS8: PersonalData = PersonalData(id(8), Some("+79990000008"), Some("Samara"), Some("Student"), None, None, Some("https://linkedin.com/stu8"), None)
  val personalDataS9: PersonalData = PersonalData(id(9), Some("+79990000009"), Some("Tambov"), Some("Student"), Some("https://vk.com/stu9"), None, Some("https://linkedin.com/stu9"), Some("https://instagram/stu9"))

  val personalDataT1: PersonalData = PersonalData(id(11), Some("+79990000011"), Some("Kazan"), Some("Teacher of Course 1."), Some("https://vk.com/t1"), Some("https://facebook.com/t1"), None, None)
  val personalDataT2: PersonalData = PersonalData(id(12), Some("+79990000012"), Some("Yakutsk"), Some("Teacher of Course 2."), Some("https://vk.com/t2"), None, Some("https://linkedin.com/t2"), None)
  val personalDataT3: PersonalData = PersonalData(id(13), Some("+79990000013"), Some("Yakutsk"), Some("Teacher of Course 3."), Some("https://vk.com/t3"), None, None, Some("https://instagram/t3"))
  val personalDataTUnregistered: PersonalData = PersonalData(id(14), None, None, None, None, None, None, None)

  val studentData1: StudentData = StudentData(id(1), 2015, Degree.Master, StudyForm.Intramural, LearningBase.Budget)
  val studentData2: StudentData = StudentData(id(2), 2015, Degree.Master, StudyForm.Intramural, LearningBase.Contract)
  val studentData3: StudentData = StudentData(id(3), 2015, Degree.Master, StudyForm.Intramural, LearningBase.Contract)
  val studentData4: StudentData = StudentData(id(4), 2017, Degree.Specialist, StudyForm.Evening, LearningBase.Budget)
  val studentData5: StudentData = StudentData(id(5), 2017, Degree.Specialist, StudyForm.Evening, LearningBase.Budget)
  val studentData6: StudentData = StudentData(id(6), 2017, Degree.Specialist, StudyForm.Evening, LearningBase.Contract)
  val studentData7: StudentData = StudentData(id(7), 2020, Degree.Bachelor, StudyForm.Extramural, LearningBase.Budget)
  val studentData8: StudentData = StudentData(id(8), 2020, Degree.Bachelor, StudyForm.Extramural, LearningBase.Contract)
  val studentData9: StudentData = StudentData(id(9), 2020, Degree.Bachelor, StudyForm.Extramural, LearningBase.Budget)

  val homework1Course1: Homework = Homework(
    id(89),
    id(31),
    "Hw1",
    "Task1",
    Timestamp.valueOf("2020-12-01 10:00:00"),
    Timestamp.valueOf("2020-12-07 23:59:59"))
  val homework2Course1: Homework = Homework(
    id(90),
    id(31),
    "Hw2",
    "Task2",
    Timestamp.valueOf("2020-12-08 10:00:00"),
    Timestamp.valueOf("2020-12-14 23:59:59"))
  val homework3Course1: Homework = Homework(
    id(91),
    id(31),
    "Hw3",
    "Task3",
    Timestamp.valueOf("2021-01-01 10:00:00"),
    Timestamp.valueOf("2021-01-07 23:59:59"))

  val homework1Course2: Homework = Homework(
    id(81),
    id(32),
    "Hw1",
    "Task1",
    Timestamp.valueOf("2020-12-14 10:00:00"),
    Timestamp.valueOf("2020-12-19 23:59:59"))
  val homework2Course2: Homework = Homework(
    id(82),
    id(32),
    "Hw2",
    "Task2",
    Timestamp.valueOf("2020-12-3 10:00:00"),
    Timestamp.valueOf("2020-12-17 23:59:59"))
  val homework3Course2: Homework = Homework(
    id(83),
    id(32),
    "Hw3",
    "Task3",
    Timestamp.valueOf("2021-02-01 10:00:00"),
    Timestamp.valueOf("2021-02-07 23:59:59"))

  val homework1Course3: Homework = Homework(
    id(84),
    id(33),
    "Hw1",
    "Task1",
    Timestamp.valueOf("2020-12-20 10:00:00"),
    Timestamp.valueOf("2020-12-29 23:59:59"))
  val homework2Course3: Homework = Homework(
    id(85),
    id(33),
    "Hw2",
    "Task2",
    Timestamp.valueOf("2021-01-08 10:00:00"),
    Timestamp.valueOf("2021-01-14 23:59:59"))
  val homework3Course3: Homework = Homework(
    id(86),
    id(33),
    "Hw3",
    "Task3",
    Timestamp.valueOf("2021-02-01 10:00:00"),
    Timestamp.valueOf("2021-02-07 23:59:59"))


  val solCourse1Hw1St1: Solution = Solution(id(82), id(1), "Some text", Timestamp.valueOf("2021-01-03 12:30:00"))
  val solCourse1Hw1St2: Solution = Solution(id(82), id(2), "Some text", Timestamp.valueOf("2021-01-04 12:30:00"))

  val solCourse2Hw2St1: Solution = Solution(id(84), id(1), "Some text", Timestamp.valueOf("2020-12-10 13:30:00"))
  val solCourse2Hw2St4: Solution = Solution(id(84), id(4), "Some text", Timestamp.valueOf("2020-12-10 13:30:00"))

  val solCourse3Hw2St7: Solution = Solution(id(86), id(7), "Some text", Timestamp.valueOf("2021-01-09 13:30:00"))
  val solCourse3Hw2St8: Solution = Solution(id(86), id(8), "Some text", Timestamp.valueOf("2021-01-09 13:30:00"))
  val solCourse3Hw2St9: Solution = Solution(id(86), id(9), "Some text", Timestamp.valueOf("2021-01-09 13:30:00"))

  val material1Course1: Material = Material(id(71), id(31), "Name1", "Some description", Timestamp.valueOf("2020-12-25 12:30:00"))
  val material2Course1: Material = Material(id(72), id(31), "Name2", "Some description", Timestamp.valueOf("2020-12-28 12:30:00"))
  val material1Course2: Material = Material(id(73), id(32), "Name1", "Some description", Timestamp.valueOf("2020-12-1 10:00:00"))
  val material2Course2: Material = Material(id(74), id(32), "Name2", "Some description", Timestamp.valueOf("2020-12-7 10:00:00"))
  val material3Course2: Material = Material(id(75), id(32), "Name3", "Some description", Timestamp.valueOf("2020-12-14 10:00:00"))
  val material1Course3: Material = Material(id(76), id(33), "Name1", "Some description", Timestamp.valueOf("2020-12-14 10:00:00"))
  val material2Course3: Material = Material(id(77), id(33), "Name2", "Some description", Timestamp.valueOf("2020-12-14 10:00:00"))


  val coursesData = Seq(course1, course2, course3)
  val groupsData = Seq(group1, group2, group3)

  def fillDatabase(implicit db: Database): TestData = {
    import Schema._

    val coursesFull = insertAndReturnAll(coursesData, courses)
    val groupsFull = insertAndReturnAll(groupsData, groups)
    val usersPartiallyFull = concatUsers(groupsFull)
    val usersFull = insertAndReturnAll(usersPartiallyFull, users)
    val verificationsFull = Seq(verificationT.copy(userId = usersFull(12).id))

    val studentDataFull = enrichStudentData(usersFull)
    insertRows(studentDataFull, studentData)

    val personalDataFull = enrichPersonalData(usersFull)
    insertRows(personalDataFull, personalData)

    val homeworksAlmostFull = enrichHomework(coursesFull)
    val homeworksFull = insertAndReturnAll(homeworksAlmostFull, homeworks)

    val materialsAlmostFull = enrichMaterials(coursesFull)
    val materialsFull = insertAndReturnAll(materialsAlmostFull, materials)

    val solutionsFull = enrichSolutions(usersFull, homeworksFull)
    insertRows(solutionsFull, solutions)

    val courseTeacherMap = Seq(
      CourseTeacher(coursesFull(0).id, usersFull(9).id),
      CourseTeacher(coursesFull(1).id, usersFull(10).id),
      CourseTeacher(coursesFull(2).id, usersFull(11).id)
    )
    insertRows(courseTeacherMap, courseTeacherLinks)

    val courseTutorMap = Seq(
      CourseTutor(coursesFull(0).id, usersFull(0).id),
      CourseTutor(coursesFull(1).id, usersFull(4).id),
      CourseTutor(coursesFull(2).id, usersFull(7).id)
    )
    insertRows(courseTutorMap, courseTutorLinks)

    val groupCourseMap = Seq(
      GroupCourse(groupsFull(0).id, coursesFull(0).id),
      GroupCourse(groupsFull(0).id, coursesFull(1).id),
      GroupCourse(groupsFull(1).id, coursesFull(1).id),
      GroupCourse(groupsFull(2).id, coursesFull(2).id),
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
      materialsFull,
      verificationsFull,
      courseTeacherMap,
      courseTutorMap,
      groupCourseMap,
    )
  }

  private def concatUsers(groupsFull: Seq[Group]): Seq[User] =
    Seq(
      student1.copy(groupId = Some(groupsFull(0).id)),
      student2.copy(groupId = Some(groupsFull(0).id)),
      student3.copy(groupId = Some(groupsFull(0).id)),
      student4.copy(groupId = Some(groupsFull(1).id)),
      student5.copy(groupId = Some(groupsFull(1).id)),
      student6.copy(groupId = Some(groupsFull(1).id)),
      student7.copy(groupId = Some(groupsFull(2).id)),
      student8.copy(groupId = Some(groupsFull(2).id)),
      student9.copy(groupId = Some(groupsFull(2).id)),
      teacher1,
      teacher2,
      teacher3,
      teacherUnregistered,
    )

  private def enrichStudentData(usersFull: Seq[User]): Seq[StudentData] =
    Seq(
      studentData1.copy(userId = usersFull(0).id),
      studentData2.copy(userId = usersFull(1).id),
      studentData3.copy(userId = usersFull(2).id),
      studentData4.copy(userId = usersFull(3).id),
      studentData5.copy(userId = usersFull(4).id),
      studentData6.copy(userId = usersFull(5).id),
      studentData7.copy(userId = usersFull(6).id),
      studentData8.copy(userId = usersFull(7).id),
      studentData9.copy(userId = usersFull(8).id),
    )

  private def enrichPersonalData(usersFull: Seq[User]): Seq[PersonalData] =
    Seq(
      personalDataS1.copy(userId = usersFull(0).id),
      personalDataS2.copy(userId = usersFull(1).id),
      personalDataS3.copy(userId = usersFull(2).id),
      personalDataS4.copy(userId = usersFull(3).id),
      personalDataS5.copy(userId = usersFull(4).id),
      personalDataS6.copy(userId = usersFull(5).id),
      personalDataS7.copy(userId = usersFull(6).id),
      personalDataS8.copy(userId = usersFull(7).id),
      personalDataS9.copy(userId = usersFull(8).id),
      personalDataT1.copy(userId = usersFull(9).id),
      personalDataT2.copy(userId = usersFull(10).id),
      personalDataT3.copy(userId = usersFull(11).id),
      personalDataTUnregistered.copy(userId = usersFull(12).id),
    )

  private def enrichHomework(coursesFull: Seq[Course]): Seq[Homework] =
    Seq(
      homework1Course1.copy(courseId = coursesFull(0).id),
      homework2Course1.copy(courseId = coursesFull(0).id),
      homework3Course1.copy(courseId = coursesFull(0).id),
      homework1Course2.copy(courseId = coursesFull(1).id),
      homework2Course2.copy(courseId = coursesFull(1).id),
      homework3Course2.copy(courseId = coursesFull(1).id),
      homework1Course3.copy(courseId = coursesFull(2).id),
      homework2Course3.copy(courseId = coursesFull(2).id),
      homework3Course3.copy(courseId = coursesFull(2).id),
    )

  private def enrichMaterials(coursesFull: Seq[Course]): Seq[Material] =
    Seq(
      material1Course1.copy(courseId = coursesFull(0).id),
      material2Course1.copy(courseId = coursesFull(0).id),
      material1Course2.copy(courseId = coursesFull(1).id),
      material2Course2.copy(courseId = coursesFull(1).id),
      material3Course2.copy(courseId = coursesFull(1).id),
      material1Course3.copy(courseId = coursesFull(2).id),
      material2Course3.copy(courseId = coursesFull(2).id)
    )

  private def enrichSolutions(usersFull: Seq[User], homeworksFull: Seq[Homework]): Seq[Solution] =
    Seq(
      solCourse1Hw1St1.copy(homeworkId = homeworksFull(0).homeworkId, studentId = usersFull(0).id),
      solCourse1Hw1St2.copy(homeworkId = homeworksFull(0).homeworkId, studentId = usersFull(1).id),
      solCourse2Hw2St1.copy(homeworkId = homeworksFull(4).homeworkId, studentId = usersFull(0).id),
      solCourse2Hw2St4.copy(homeworkId = homeworksFull(4).homeworkId, studentId = usersFull(3).id),
      solCourse3Hw2St7.copy(homeworkId = homeworksFull(7).homeworkId, studentId = usersFull(6).id),
      solCourse3Hw2St8.copy(homeworkId = homeworksFull(7).homeworkId, studentId = usersFull(7).id),
      solCourse3Hw2St9.copy(homeworkId = homeworksFull(7).homeworkId, studentId = usersFull(8).id)
    )

  def truncateTables(implicit db: Database): Unit = {
    val deleteSchema = Schema.tables.map(_.schema.truncate)
    val setup = DBIO.sequence(deleteSchema)
    Await.ready(db.run(setup), 5.seconds)
  }

  def dropTables(implicit db: Database): Unit = {
    val deleteSchema = Schema.tables.map(_.schema.dropIfExists)
    val setup = DBIO.sequence(deleteSchema)
    Await.ready(db.run(setup), 5.seconds)
  }

  private def insertAndReturnAll[T, E <: Table[T]]
      (rows: Seq[T], tableQuery: TableQuery[E])(implicit db: Database): Seq[T] = {
    val action = (tableQuery ++= rows).andThen(tableQuery.result)
    Await.result(db.run(action), 5.seconds)
  }

  private def insertRows[T, E <: Table[T]]
      (rows: Seq[T], tableQuery: TableQuery[E])(implicit db: Database): Unit = {
    Await.result(db.run(DBIO.seq(tableQuery ++= rows)), 5.seconds)
  }

  private def id[T](v: Long): Id[T] = new Id[T](v)
}
