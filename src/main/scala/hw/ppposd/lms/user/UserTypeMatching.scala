package hw.ppposd.lms.user

import hw.ppposd.lms.group.Group
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

trait UserTypeMatching {
  def matchUserType[T](userCommons: UserCommons, userId: Id[User])
                      (ifStudent: Id[Group] => Future[T],
                       ifTeacher: => Future[T])
                      (implicit ec: ExecutionContext): Future[T] = {
    val groupIdOptFuture = userCommons.getUserGroupId(userId)
    groupIdOptFuture.flatMap {
      case Some(groupId) => ifStudent(groupId)
      case None => ifTeacher
    }
  }
}
