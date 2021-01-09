package hw.ppposd.lms.user

import hw.ppposd.lms.group.Group
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ExecutionContext, Future}

trait UserCommons {
  def getUserGroupId(userId: Id[User]): Future[Option[Id[Group]]]
  def enrichUsers(userIds: Seq[Id[User]]): Future[Seq[UserBrief]]
}

class UserCommonsImpl(implicit db: Database, ec: ExecutionContext) extends UserCommons {
  import hw.ppposd.lms.Schema._

  override def getUserGroupId(userId: Id[User]): Future[Option[Id[Group]]] =
    db.run(users.filter(_.id === userId).map(_.groupId).result.head)

  override def enrichUsers(userIds: Seq[Id[User]]): Future[Seq[UserBrief]] =
    db.run(users.filter(_.id inSet userIds).map(x => (x.id, x.fullName)).result
      .map(seq => seq.map((UserBrief.apply _).tupled)))
}
