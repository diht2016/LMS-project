package hw.ppposd.lms.user

import hw.ppposd.lms.group.Group
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait UserRepository {
  def create(name: String, fullName: String, groupId: Option[Id[Group]]): Future[Id[User]]
  def createRegistered(name: String, fullName: String,
                       email: String, passwordHash: String,
                       groupId: Option[Id[Group]]): Future[Id[User]]
  def find(id: Id[User]): Future[Option[User]]
  def list(): Future[Seq[User]]
}

class UserRepositoryImpl extends UserRepository {
  import hw.ppposd.lms.Schema._

  override def create(name: String, fullName: String, groupId: Option[Id[Group]]): Future[Id[User]] =
    db.run((users returning users.map(_.id))
      += User(Id.auto, fullName, "", "", groupId))

  override def createRegistered(name: String, fullName: String,
                                email: String, passwordHash: String,
                                groupId: Option[Id[Group]]): Future[Id[User]] =
    db.run((users returning users.map(_.id))
      += User(Id.auto, fullName, email, passwordHash, groupId))

  override def find(id: Id[User]): Future[Option[User]] =
    db.run(users.filter(_.id === id).result.headOption)

  override def list(): Future[Seq[User]] =
    db.run(users.result)
}
