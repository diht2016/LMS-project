package hw.ppposd.lms.course.tutor

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import hw.ppposd.lms.base.JsonSerializer.toJsonString
import hw.ppposd.lms.base.RouteSpecBase
import hw.ppposd.lms.course.AccessRepository
import hw.ppposd.lms.user.{UserBrief, UserCommons}

import scala.concurrent.Future

class TutorControllerSpec extends RouteSpecBase {
  import TutorControllerSpec._

  "TutorController" should "return full list of course tutors if user is a course teacher" in new TestWiring {
    tutorRepoMock.listCourseTutorIds _ expects sampleCourseId returns
      Future.successful(sampleTutorIds)
    userCommonsMock.enrichUsers _ expects sampleTutorIds returns
      Future.successful(sampleTutorBriefs)

    Get("/tutors") ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleListResponse
    }
  }

  it should "add new tutor if user is a course teacher and tutor is a course student" in new TestWiring {
    (tutorRepoMock.add _).expects (sampleCourseId, sampleTutorId) returns
      Future.successful(1)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (accessRepoMock.isCourseStudent _).expects (sampleTutorId, sampleCourseId) returns
      Future.successful(true)

    Post(s"/tutors/$sampleTutorId") ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  it should "respond with 404 if tutor already added" in new TestWiring {
    (tutorRepoMock.add _).expects (sampleCourseId, sampleTutorId) returns
      Future.successful(0)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (accessRepoMock.isCourseStudent _).expects (sampleTutorId, sampleCourseId) returns
      Future.successful(true)

    Post(s"/tutors/$sampleTutorId") ~> route ~> check {
      status shouldBe StatusCodes.NotFound
      responseAs[String] shouldBe errorNotFoundResponse
    }
  }

  it should "respond with 400 if trying to add not a course student" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (accessRepoMock.isCourseStudent _).expects (sampleTutorId, sampleCourseId) returns
      Future.successful(false)

    Post(s"/tutors/$sampleTutorId") ~> route ~> check {
      status shouldBe StatusCodes.BadRequest
      responseAs[String] shouldBe errorBadIdResponse
    }
  }

  it should "respond with 403 if non-teacher is trying to add new tutor" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)
    (accessRepoMock.isCourseStudent _).expects (sampleTutorId, sampleCourseId) returns
      Future.successful(true)

    Post(s"/tutors/$sampleTutorId") ~> route ~> check {
      status shouldBe StatusCodes.Forbidden
      responseAs[String] shouldBe errorAccessResponse
    }
  }

  it should "delete tutor if user is a course teacher and tutor is a course student" in new TestWiring {
    (tutorRepoMock.delete _).expects (sampleCourseId, sampleTutorId) returns
      Future.successful(1)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (accessRepoMock.isCourseStudent _).expects (sampleTutorId, sampleCourseId) returns
      Future.successful(true)

    Delete(s"/tutors/$sampleTutorId") ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  it should "respond with 404 if nothing to delete" in new TestWiring {
    (tutorRepoMock.delete _).expects (sampleCourseId, sampleTutorId) returns
      Future.successful(0)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (accessRepoMock.isCourseStudent _).expects (sampleTutorId, sampleCourseId) returns
      Future.successful(true)

    Delete(s"/tutors/$sampleTutorId") ~> route ~> check {
      status shouldBe StatusCodes.NotFound
      responseAs[String] shouldBe errorNotFoundResponse
    }
  }

  it should "respond with 400 if trying to delete not a course student" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)
    (accessRepoMock.isCourseStudent _).expects (sampleTutorId, sampleCourseId) returns
      Future.successful(false)

    Delete(s"/tutors/$sampleTutorId") ~> route ~> check {
      status shouldBe StatusCodes.BadRequest
      responseAs[String] shouldBe errorBadIdResponse
    }
  }

  it should "respond with 403 if non-teacher is trying to delete tutor" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)
    (accessRepoMock.isCourseStudent _).expects (sampleTutorId, sampleCourseId) returns
      Future.successful(true)

    Delete(s"/tutors/$sampleTutorId") ~> route ~> check {
      status shouldBe StatusCodes.Forbidden
      responseAs[String] shouldBe errorAccessResponse
    }
  }

  trait TestWiring {
    val accessRepoMock: AccessRepository = mock[AccessRepository]
    val userCommonsMock: UserCommons = mock[UserCommons]
    val tutorRepoMock: TutorRepository = mock[TutorRepository]
    val controller = new TutorController(tutorRepoMock, accessRepoMock, userCommonsMock)
    val route: Route = controller.route(sampleUserId, sampleCourseId)
  }
}

object TutorControllerSpec {
  import hw.ppposd.lms.SampleDatabaseContent._

  private val sampleUserId = teacher2.id
  private val sampleCourseId = course1.id
  private val sampleTutorId = student1.id
  private val sampleTutorBrief = UserBrief(student1.id, student1.fullName)
  private val sampleTutorBrief2 = UserBrief(student2.id, student2.fullName)
  private val sampleTutorBriefs = Seq(sampleTutorBrief, sampleTutorBrief2)
  private val sampleTutorIds = sampleTutorBriefs.map(_.id)
  private val sampleListResponse = toJsonString(sampleTutorBriefs)
  private val errorAccessResponse = """{"error":"not permitted to manage tutors"}"""
  private val errorNotFoundResponse = """{"error":"nothing to update"}"""
  private val errorBadIdResponse = """{"error":"user is not a course student"}"""
}
