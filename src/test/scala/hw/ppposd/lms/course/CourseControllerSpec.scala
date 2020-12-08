package hw.ppposd.lms.course

import hw.ppposd.lms.SpecBase
import hw.ppposd.lms.util.Id
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CourseControllerSpec extends SpecBase {
  private val sampleCourse = Course(new Id[Course](1), "name", "desc")
  private val sampleResponse = Json.toJson(Seq(sampleCourse))

  "getCourses" should "return list of courses" in new TestWiring {
    repoMock.list _ expects() returns Future.successful(Seq(sampleCourse))

    controller.listCourses() should be (sampleResponse)
  }

  private trait TestWiring {
    protected val repoMock = mock[CourseRepository]
    protected val controller = new CourseController(stub[ControllerComponents], repoMock)
  }
}
