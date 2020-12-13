package hw.ppposd.lms.group

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.access.AccessService
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class GroupController(accessService: AccessService)
                     (implicit ec: ExecutionContext) extends Controller {
  def route(userId: Id[User]): Route = {
    pathEndOrSingleSlash {
      get { listGroupStudents(userId) }
    }
  }

  def listGroupStudents(userId: Id[User]): Future[Option[Seq[Id[User]]]] = {
    accessService.matchUserType(userId) (
      ifStudent = groupId => accessService.listStudentIdsByGroupId(groupId).map(Some(_)),
      ifTeacher = Future.successful(None)
    )
  }
}
