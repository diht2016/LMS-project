package hw.ppposd.lms.group

import akka.http.scaladsl.model.StatusCodes
import hw.ppposd.lms.base.JsonSerializer.toJsonString
import hw.ppposd.lms.base.RouteSpecBase
import hw.ppposd.lms.user.{User, UserBrief, UserCommons}
import hw.ppposd.lms.util.Id

import scala.concurrent.Future

class GroupControllerSpec extends RouteSpecBase {
  import GroupControllerSpec._

  "listUserCourses" should "return list of group students for student" in new TestWiring {
    userCommonsMock.getUserGroupId _ expects sampleUserId returns
      Future.successful(Some(sampleGroupId))
    groupRepoMock.listGroupStudentIds _ expects sampleGroupId returns
      Future.successful(sampleStudentIds)
    userCommonsMock.enrichUsers _ expects sampleStudentIds returns
      Future.successful(sampleStudentBriefs)

    Get("/groups/my") ~> route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe sampleStudentsResponse
    }
  }

  it should "respond with 404 if user is a teacher" in new TestWiring {
    userCommonsMock.getUserGroupId _ expects sampleUserId returns
      Future.successful(None)

    Get("/groups/my") ~> route ~> check {
      status shouldBe StatusCodes.NotFound
      responseAs[String] shouldBe sampleErrorResponse
    }
  }

  private trait TestWiring {
    import akka.http.scaladsl.server.Route

    protected val userCommonsMock: UserCommons = mock[UserCommons]
    protected val groupRepoMock: GroupRepository = mock[GroupRepository]

    protected val controller = new GroupController(groupRepoMock, userCommonsMock)
    protected val route: Route = controller.route(sampleUserId)
  }
}

object GroupControllerSpec {
  private val sampleUserId = new Id[User](234)
  private val sampleGroupId = new Id[Group](21)
  private val sampleStudentBriefs = Seq(
    UserBrief(new Id[User](231), "student1 name"),
    UserBrief(new Id[User](152), "student2 name"),
    UserBrief(new Id[User](217), "student3 name"),
  )
  private val sampleStudentIds = sampleStudentBriefs.map(_.id)

  private val sampleStudentsResponse = toJsonString(sampleStudentBriefs)
  private val sampleErrorResponse = """{"error":"teachers do not belong to any group"}"""
}


