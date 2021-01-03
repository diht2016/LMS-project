package hw.ppposd.lms.group

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.access.{AccessService, UserBrief}
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class GroupController(accessService: AccessService)
                     (implicit ec: ExecutionContext) extends Controller {
  def route(userId: Id[User]): Route = {
    (pathEndOrSingleSlash & get) {
      listGroupStudents(userId)
    }
  }

  def listGroupStudents(userId: Id[User]): Future[Seq[UserBrief]] = {
    accessService.matchUserType(userId) (
      ifStudent = groupId => accessService.listGroupStudentBriefs(groupId),
      ifTeacher = ApiError(401, "teachers do not belong to any group")
    )
  }
}
