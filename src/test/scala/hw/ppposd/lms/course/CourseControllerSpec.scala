package hw.ppposd.lms.course

import hw.ppposd.lms.SpecBase
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.Future

class CourseControllerSpec extends SpecBase {
  private val sampleUserId = new Id[User](234)
  private val sampleCourse = Course(new Id[Course](1), "test", "desc")
  private val sampleResponse = """[{"id":1,"name":"test","description":"desc"}]"""

  "getCourses" should "return list of courses" in new TestWiring {
    repoMock.list _ expects() returns Future.successful(Seq(sampleCourse)) once()

    private val route = controller.route(sampleUserId)

    Get() ~> route ~> check {
      responseAs[String] should be (sampleResponse)
    }
  }

  private trait TestWiring {
    protected val repoMock = mock[CourseRepository]
    protected val controller = new CourseController(repoMock)
  }
}
