package hw.ppposd.lms.group

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.user.{User, UserBrief, UserCommons, UserTypeMatching}
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class GroupController(groupRepo: GroupRepository, userCommons: UserCommons)
                     (implicit ec: ExecutionContext) extends Controller with UserTypeMatching {
  def route(userId: Id[User]): Route = {
    (pathEndOrSingleSlash & get) {
      listSameGroupStudents(userId)
    }
  }

  def listSameGroupStudents(userId: Id[User]): Future[Seq[UserBrief]] = {
    matchUserType(userCommons, userId) (
      ifStudent = groupId =>
        groupRepo.listGroupStudentIds(groupId)
          .flatMap(userCommons.enrichUsers),
      ifTeacher = ApiError(401, "teachers do not belong to any group")
    )
  }
}
