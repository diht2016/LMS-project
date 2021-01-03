package hw.ppposd.lms.course

import hw.ppposd.lms.SpecBase
import hw.ppposd.lms.access.{AccessRepository, AccessService}
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id


import scala.concurrent.Future



class CourseControllerSpec extends SpecBase {
  import CourseControllerSpec._

  private trait TestWiring {
    protected val accessRepoMock = mock[AccessRepository]
    protected val courseRepoMock = mock[CourseRepository]
    protected val accessService = new AccessService(accessRepoMock)
    protected val controller = new CourseController(courseRepoMock, accessService)
  }


  "getCourses" should "return list of courses" in new TestWiring {
    accessRepoMock.getUserGroupId _ expects sampleUserId returns
      Future.successful(Some(sampleGroupId)) repeat 1
    accessRepoMock.listGroupCourseIds _ expects sampleGroupId returns
      Future.successful(Seq(sampleCourseId)) repeat 1
    courseRepoMock.find _ expects sampleCourseId returns
      Future.successful(Some(sampleCourse)) repeat 1

    private val route = controller.route(sampleUserId)

    Get() ~> route ~> check {
      responseAs[String] should be (sampleResponse)
    }
  }
}

object CourseControllerSpec {
  val sampleUserId = new Id[User](234)
  val sampleGroupId = new Id[Group](26)
  val sampleCourseId = new Id[Course](11)
  val sampleCourse = Course(sampleCourseId, "test", "desc")
  val sampleResponse =
    s"""[{"id":${sampleCourseId.value},"name":"test","description":"desc"}]"""


}