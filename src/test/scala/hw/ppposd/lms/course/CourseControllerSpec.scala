package hw.ppposd.lms.course

import hw.ppposd.lms.SpecBase
import hw.ppposd.lms.access.{AccessRepository, AccessService}
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.Future

class CourseControllerSpec extends SpecBase {
  private val sampleUserId = new Id[User](234)
  private val sampleGroupId = new Id[Group](26)
  private val sampleCourseId = new Id[Course](11)
  private val sampleCourse = Course(sampleCourseId, "test", "desc")
  private val sampleResponse =
    s"""[{"id":${sampleCourseId.value},"name":"test","description":"desc"}]"""

  "getCourses" should "return list of courses" in new TestWiring {
    accessRepoMock.getUserGroupId _ expects sampleUserId returns
      Future.successful(Some(sampleGroupId)) once()
    accessRepoMock.listGroupCourseIds _ expects sampleGroupId returns
      Future.successful(Seq(sampleCourseId)) once()
    courseRepoMock.find _ expects sampleCourseId returns
      Future.successful(Some(sampleCourse)) once()

    private val route = controller.route(sampleUserId)

    Get() ~> route ~> check {
      responseAs[String] should be (sampleResponse)
    }
  }

  private trait TestWiring {
    protected val accessRepoMock = mock[AccessRepository]
    protected val courseRepoMock = mock[CourseRepository]
    protected val accessService = new AccessService(accessRepoMock)
    protected val controller = new CourseController(courseRepoMock, accessService)
  }
}
