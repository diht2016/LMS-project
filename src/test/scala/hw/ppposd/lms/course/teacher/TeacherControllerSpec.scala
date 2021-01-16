package hw.ppposd.lms.course.teacher

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import hw.ppposd.lms.base.JsonSerializer.toJsonString
import hw.ppposd.lms.base.RouteSpecBase
import hw.ppposd.lms.user.{UserBrief, UserCommons}

import scala.concurrent.Future

class TeacherControllerSpec extends RouteSpecBase {
  import TeacherControllerSpec._

  "TeacherController" should "return full list of course teachers if user is a course teacher" in new TestWiring {
    teacherRepoMock.listCourseTeacherIds _ expects sampleCourseId returns
      Future.successful(sampleTeacherIds)
    userCommonsMock.enrichUsers _ expects sampleTeacherIds returns
      Future.successful(sampleTeacherBriefs)

    Get("/teachers") ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleListResponse
    }
  }

  trait TestWiring {
    val userCommonsMock: UserCommons = mock[UserCommons]
    val teacherRepoMock: TeacherRepository = mock[TeacherRepository]
    val controller = new TeacherController(teacherRepoMock, userCommonsMock)
    val route: Route = controller.route(sampleUserId, sampleCourseId)
  }
}

object TeacherControllerSpec {
  import hw.ppposd.lms.SampleDatabaseContent._

  private val sampleUserId = student1.id
  private val sampleCourseId = course1.id
  private val sampleTeacherBrief = UserBrief(teacher1.id, teacher1.fullName)
  private val sampleTeacherBrief2 = UserBrief(teacher2.id, teacher2.fullName)
  private val sampleTeacherBriefs = Seq(sampleTeacherBrief, sampleTeacherBrief2)
  private val sampleTeacherIds = sampleTeacherBriefs.map(_.id)
  private val sampleListResponse = toJsonString(sampleTeacherBriefs)
}
