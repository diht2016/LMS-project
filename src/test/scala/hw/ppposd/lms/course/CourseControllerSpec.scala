package hw.ppposd.lms.course

import akka.http.scaladsl.model.StatusCodes
import hw.ppposd.lms.base.RouteSpecBase
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.{User, UserCommons}
import hw.ppposd.lms.util.Id

import scala.concurrent.Future

class CourseControllerSpec extends RouteSpecBase {
  import CourseControllerSpec._

  "listUserCourses" should "return list of group courses for student" in new TestWiring {
    userCommonsMock.getUserGroupId _ expects sampleUserId returns
      Future.successful(Some(sampleGroupId))
    courseRepoMock.listGroupCourseIds _ expects sampleGroupId returns
      Future.successful(Seq(sampleCourseId))
    courseRepoMock.findCourses _ expects Seq(sampleCourseId) returns
      Future.successful(Seq(sampleCourse))

    Get() ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleCoursesResponse
    }
  }

  it should "return list of teaching courses for teacher" in new TestWiring {
    userCommonsMock.getUserGroupId _ expects sampleUserId returns
      Future.successful(None)
    courseRepoMock.listTeacherCourseIds _ expects sampleUserId returns
      Future.successful(Seq(sampleCourseId))
    courseRepoMock.findCourses _ expects Seq(sampleCourseId) returns
      Future.successful(Seq(sampleCourse))

    Get() ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleCoursesResponse
    }
  }

  "getCourse" should "return course by id" in new TestWiring {
    courseRepoMock.find _ expects sampleCourseId returns
      Future.successful(Some(sampleCourse))

    Get(s"/${sampleCourseId.value}") ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleCourseResponse
    }
  }

  it should "respond with 404 if no course found" in new TestWiring {
    courseRepoMock.find _ expects sampleCourseId returns
      Future.successful(None)

    private val sampleResponse =
      s"""{"error":"course not found"}"""

    Get(s"/${sampleCourseId.value}") ~> route ~> check {
      status shouldBe StatusCodes.NotFound
      responseAs[String] shouldBe sampleResponse
    }
  }

  // todo: fix java.lang.NullPointerException on mock creation
  //"CourseController" should "delegate calls to TeacherController" in new TestWiring {
  //  val innerControllerMock: TeacherController = mock[TeacherController]
  //  (innerControllerMock.route _).expects (sampleUserId, sampleCourseId) returns
  //    complete("delegated")
  //  (wiringMock.teacherController _).expects() returns innerControllerMock
  //
  //  Get("/teachers/") ~> route ~> check {
  //    status shouldBe StatusCodes.OK
  //    responseAs[String] shouldBe "delegated"
  //  }
  //}

  private trait TestWiring {
    import akka.http.scaladsl.server.Route

    protected val userCommonsMock: UserCommons = mock[UserCommons]
    protected val courseRepoMock: CourseRepository = mock[CourseRepository]
    protected val wiringMock: CourseWiring = mock[CourseWiring]

    protected val controller = new CourseController(courseRepoMock, userCommonsMock, wiringMock)
    protected val route: Route = controller.route(sampleUserId)
  }
}

object CourseControllerSpec {
  private val sampleUserId = new Id[User](234)
  private val sampleGroupId = new Id[Group](26)
  private val sampleCourseId = new Id[Course](11)
  private val sampleCourse = Course(sampleCourseId, "test course", "sample desc")
  private val sampleCourseResponse =
    s"""{"id":${sampleCourseId.value},"name":"test course","description":"sample desc"}"""
  private val sampleCoursesResponse = s"[$sampleCourseResponse]"
}
