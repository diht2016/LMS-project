package hw.ppposd.lms.course.homework

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import hw.ppposd.lms.base.JsonSerializer.toJsonString
import hw.ppposd.lms.base.RouteSpecBase
import hw.ppposd.lms.course.AccessRepository
import hw.ppposd.lms.course.homework.HomeworkController.HomeworkEntity

import scala.concurrent.Future

class HomeworkControllerSpec extends RouteSpecBase {
  import HomeworkControllerSpec._

  "HomeworkController" should "return full list of course homeworks if user is a course teacher" in new TestWiring {
    homeworkRepoMock.list _ expects sampleCourseId returns
      Future.successful(sampleHomeworks)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)

    Get() ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleListResponse
    }
  }

  it should "return full list of course homeworks if user is not a course teacher" in new TestWiring {
    (homeworkRepoMock.listStarted _).expects (sampleCourseId, *) returns
      Future.successful(sampleHomeworks)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)

    Get() ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleListResponse
    }
  }

  it should "create new homework if user is a course teacher" in new TestWiring {
    (homeworkRepoMock.add _).expects (sampleCourseId, sampleHomework.name, sampleHomework.description,
      sampleHomework.startDate, sampleHomework.deadlineDate) returns
      Future.successful(sampleHomeworkId)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)

    PostJson("/", sampleHomeworkEntity) ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleHomeworkId.toString
    }
  }

  it should "respond with 403 if non-teacher is trying to create new homework" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)

    PostJson("/", sampleHomeworkEntity) ~> route ~> check {
      status shouldBe StatusCodes.Forbidden
      responseAs[String] shouldBe errorResponse
    }
  }

  it should "edit homework if user is a course teacher" in new TestWiring {
    (homeworkRepoMock.edit _).expects (sampleHomeworkId, sampleHomework.name, sampleHomework.description,
      sampleHomework.startDate, sampleHomework.deadlineDate) returns
      Future.successful(1)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)

    PutJson(s"/$sampleHomeworkId", sampleHomeworkEntity) ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  it should "respond with 403 if non-teacher is trying to edit homework" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)

    PutJson(s"/$sampleHomeworkId", sampleHomeworkEntity) ~> route ~> check {
      status shouldBe StatusCodes.Forbidden
      responseAs[String] shouldBe errorResponse
    }
  }

  it should "delete homework if user is a course teacher" in new TestWiring {
    (homeworkRepoMock.delete _).expects (sampleHomeworkId) returns
      Future.successful(1)
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(true)

    Delete(s"/$sampleHomeworkId") ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  it should "respond with 403 if non-teacher is trying to delete homework" in new TestWiring {
    (accessRepoMock.isCourseTeacher _).expects (sampleUserId, sampleCourseId) returns
      Future.successful(false)

    Delete(s"/$sampleHomeworkId") ~> route ~> check {
      status shouldBe StatusCodes.Forbidden
      responseAs[String] shouldBe errorResponse
    }
  }

  trait TestWiring {
    val accessRepoMock: AccessRepository = mock[AccessRepository]
    val homeworkRepoMock: HomeworkRepository = mock[HomeworkRepository]
    val controller = new HomeworkController(homeworkRepoMock, accessRepoMock)
    val route: Route = controller.route(sampleUserId, sampleCourseId)
  }
}

object HomeworkControllerSpec {
  import hw.ppposd.lms.SampleDatabaseContent._

  private val sampleUserId = student1.id
  private val sampleCourseId = homeworkAlgebra1.courseId
  private val sampleHomeworkId = homeworkAlgebra1.homeworkId
  private val sampleHomework = homeworkAlgebra1
  private val sampleHomeworkEntity = HomeworkEntity(
    sampleHomework.name,
    sampleHomework.description,
    sampleHomework.startDate,
    sampleHomework.deadlineDate
  )
  private val sampleHomeworks = Seq(homeworkAlgebra1, homeworkAlgebra2, homeworkAlgebra3)
  private val sampleListResponse = toJsonString(sampleHomeworks)
  private val errorResponse = """{"error":"not permitted to manage homeworks"}"""
}
