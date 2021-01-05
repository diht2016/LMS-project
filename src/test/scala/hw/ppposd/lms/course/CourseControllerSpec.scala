package hw.ppposd.lms.course

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import hw.ppposd.lms.base.RouteSpecBase
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.Future

class CourseControllerSpec extends RouteSpecBase {
  import CourseControllerSpec._

  "CourseController" should "return list of courses" in new TestWiring {
    accessRepoMock.findUserGroupId _ expects sampleUserId returns
      Future.successful(Some(sampleGroupId))
    courseRepoMock.listGroupCourseIds _ expects sampleGroupId returns
      Future.successful(Seq(sampleCourseId))
    courseRepoMock.findCourses _ expects Seq(sampleCourseId) returns
      Future.successful(Seq(sampleCourse))

    private val sampleResponse =
      s"""[{"id":${sampleCourseId.value},"name":"test course","description":"sample desc"}]"""

    Get() ~> route ~> check {
      status should be (StatusCodes.OK)
      responseAs[String] should be (sampleResponse)
    }
  }

  it should "return course by id" in new TestWiring {
    courseRepoMock.find _ expects sampleCourseId returns
      Future.successful(Some(sampleCourse))

    private val sampleResponse =
      s"""{"id":${sampleCourseId.value},"name":"test course","description":"sample desc"}"""

    Get(s"/${sampleCourseId.value}") ~> route ~> check {
      status should be (StatusCodes.OK)
      responseAs[String] should be (sampleResponse)
    }
  }

  it should "respond with 404 if no course found" in new TestWiring {
    courseRepoMock.find _ expects sampleCourseId returns
      Future.successful(None)

    private val sampleResponse =
      s"""{"error":"course not found"}"""

    Get(s"/${sampleCourseId.value}") ~> route ~> check {
      status should be (StatusCodes.NotFound)
      responseAs[String] should be (sampleResponse)
    }
  }

  private trait TestWiring {
    protected val accessRepoMock = mock[AccessRepository]
    protected val courseRepoMock = mock[CourseRepository]
    protected val wiringMock = mock[CourseWiring]
    protected val controller = new CourseController(courseRepoMock, accessRepoMock, wiringMock)
    protected val route: Route = controller.route(sampleUserId)
  }
}

object CourseControllerSpec {
  private val sampleUserId = new Id[User](234)
  private val sampleGroupId = new Id[Group](26)
  private val sampleCourseId = new Id[Course](11)
  private val sampleCourse = Course(sampleCourseId, "test course", "sample desc")
}
