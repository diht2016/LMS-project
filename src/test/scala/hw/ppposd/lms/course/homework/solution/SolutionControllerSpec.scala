package hw.ppposd.lms.course.homework.solution

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import hw.ppposd.lms.base.JsonSerializer.toJsonString
import hw.ppposd.lms.base.RouteSpecBase
import hw.ppposd.lms.course.AccessRepository
import hw.ppposd.lms.course.homework.solution.SolutionController.{GroupSolutionsInfo, SolutionEntity, StudentSolutionInfo}
import hw.ppposd.lms.course.homework.HomeworkRepository
import hw.ppposd.lms.group.GroupRepository
import hw.ppposd.lms.user.{UserBrief, UserCommons}

import scala.concurrent.Future

class SolutionControllerSpec extends RouteSpecBase {
  import SolutionControllerSpec._

  "SolutionController" should "return 403 if not a teacher requires list of students' solutions" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)
    Get("/solutions") ~> route ~> check {
      status shouldBe StatusCodes.Forbidden
      responseAs[String] shouldBe notTeacherErrorResponse
    }
  }

  it should "return grouped list of students' solutions if user is a teacher" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (groupRepoMock.listGroupsAssignedToCourse _).expects (sampleCourseId) returns
      Future.successful(groupsAssignedToCourse2)
    (groupRepoMock.listGroupStudentIds _).expects (groupsAssignedToCourse2(0).id) returns
      Future.successful(group1Students.map(_.id))
    (groupRepoMock.listGroupStudentIds _).expects (groupsAssignedToCourse2(1).id) returns
      Future.successful(group2Students.map(_.id))
    (solutionRepoMock.find _).expects (sampleHomeworkId, group1Students(0).id) returns
      Future.successful(Some(student1Sol))
    (solutionRepoMock.find _).expects (sampleHomeworkId, group2Students(0).id) returns
      Future.successful(None)

    private val studentsBrief1 = group1Students.map(u => UserBrief(u.id, u.fullName))
    (userCommonsMock.enrichUsers _).expects (group1Students.map(_.id)) returns
      Future.successful(studentsBrief1)
    private val studentsBrief2 = group2Students.map(u => UserBrief(u.id, u.fullName))
    (userCommonsMock.enrichUsers _).expects (group2Students.map(_.id)) returns
      Future.successful(studentsBrief2)

    val solList = Seq(
      GroupSolutionsInfo(groupsAssignedToCourse2(0), Seq(StudentSolutionInfo(studentsBrief1(0), true))),
      GroupSolutionsInfo(groupsAssignedToCourse2(1), Seq(StudentSolutionInfo(studentsBrief2(0), false)))
    )

    Get("/solutions") ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe toJsonString(solList)
    }
  }

  it should "respond with 403 if not a teacher requires solution's text" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)

    Get(s"/solutions/$sampleStudentId") ~> route ~> check {
      status shouldBe StatusCodes.Forbidden
      responseAs[String] shouldBe notTeacherErrorResponse
    }
  }

  it should "return solution's text if user is a teacher" in new TestWiring {
    (solutionRepoMock.find _).expects (sampleHomeworkId, sampleStudentId) returns
      Future.successful(Some(sampleSolution))
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)

    Get(s"/solutions/$sampleStudentId") ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe toJsonString(sampleSolution.text)
    }
  }

  it should "respond with 404 if a student tries to upload a solution to a non-existent homework" in new TestWiring {
    (accessRepoMock.isCourseStudent _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (homeworkRepoMock.findAndCheckAvailability _).expects (sampleHomeworkId, *) returns
      Future.successful(None)

    PostJson("/solutions", sampleSolutionEntity) ~> route ~> check {
      status shouldBe StatusCodes.NotFound
      responseAs[String] shouldBe homeworkDoesNotExistsResponse
    }
  }

  it should "respond with 403 if a student tries to upload a solution when the deadline has already been passed" in new TestWiring {
    (accessRepoMock.isCourseStudent _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (homeworkRepoMock.findAndCheckAvailability _).expects (sampleHomeworkId, *) returns
      Future.successful(Some(sampleHomework, false))

    PostJson("/solutions", sampleSolutionEntity) ~> route ~> check {
      status shouldBe StatusCodes.Forbidden
      responseAs[String] shouldBe notAvailableErrorResponse
    }
  }

  it should "respond with 403 if not a student tries to upload a solution" in new TestWiring {
    (accessRepoMock.isCourseStudent _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)

    PostJson("/solutions", sampleSolutionEntity) ~> route ~> check {
      status shouldBe StatusCodes.Forbidden
      responseAs[String] shouldBe notStudentErrorResponse
    }
  }

  it should "set student's solution for the homework" in new TestWiring {
    (accessRepoMock.isCourseStudent _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (homeworkRepoMock.findAndCheckAvailability _).expects (sampleHomeworkId, *) returns
      Future.successful(Some(sampleHomework, true))
    (solutionRepoMock.set _).expects (sampleHomeworkId, sampleUserId, sampleSolutionEntity.text) returns
      Future.successful(1)

    PostJson("/solutions", sampleSolutionEntity) ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  trait TestWiring {
    val accessRepoMock: AccessRepository = mock[AccessRepository]
    val solutionRepoMock: SolutionRepository = mock[SolutionRepository]
    val homeworkRepoMock: HomeworkRepository = mock[HomeworkRepository]
    val groupRepoMock: GroupRepository = mock[GroupRepository]
    val userCommonsMock: UserCommons = mock[UserCommons]
    val controller = new SolutionController(accessRepoMock, solutionRepoMock, homeworkRepoMock, groupRepoMock, userCommonsMock)

    val route: Route = controller.route(sampleUserId, sampleCourseId, sampleHomeworkId)
  }
}

object SolutionControllerSpec {
  import hw.ppposd.lms.SampleDatabaseContent._

  private val sampleUserId = teacher2.id

  private val sampleCourseId = course2.id
  private val groupsAssignedToCourse2 = Seq(group1, group2)
  private val group1Students = Seq(student1)
  private val student1Sol = solCourse2Hw2St1
  private val group2Students = Seq(student5)

  private val sampleHomeworkId = homework2Course2.homeworkId
  private val sampleHomework = homework2Course2
  private val sampleStudentId = student2.id
  private val sampleSolution = solCourse1Hw1St1
  private val sampleSolutionEntity = SolutionEntity("some text")

  private val homeworkDoesNotExistsResponse = s"""{"error":"homework with id=$sampleHomeworkId does not exists."}"""
  private val notTeacherErrorResponse = """{"error":"not permitted to view homework solutions"}"""
  private val notAvailableErrorResponse = """{"error":"homework is not available for solving"}"""
  private val notStudentErrorResponse = """{"error":"not permitted to submit homework solution"}"""
}
