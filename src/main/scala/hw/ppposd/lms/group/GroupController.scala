package hw.ppposd.lms.group

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.user.{User, UserBrief}
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class GroupController(groupRepo: GroupRepository)
                     (implicit ec: ExecutionContext) extends Controller {
  def route(userId: Id[User]): Route = {
    (pathEndOrSingleSlash & get) {
      listSameGroupStudents(userId)
    }
  }

  def listSameGroupStudents(userId: Id[User]): Future[Seq[UserBrief]] = {
    groupRepo.findUserGroupId(userId)
      .flatMap {
        case Some(groupId) => groupRepo.listGroupStudentBriefs(groupId)
        case None => ApiError(401, "teachers do not belong to any group")
      }
  }
}
