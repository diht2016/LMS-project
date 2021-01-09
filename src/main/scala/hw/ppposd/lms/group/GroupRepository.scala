package hw.ppposd.lms.group

import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait GroupRepository {
  def create(name: String, department: String, courseNumber: Int): Future[Id[Group]]
  def find(id: Id[Group]): Future[Option[Group]]
  def list(): Future[Seq[Group]]
  def listGroupStudentIds(groupId: Id[Group]): Future[Seq[Id[User]]]
}

class GroupRepositoryImpl(implicit db: Database) extends GroupRepository {
  import hw.ppposd.lms.Schema._

  override def create(name: String, department: String, courseNumber: Int): Future[Id[Group]] =
    db.run((groups returning groups.map(_.id))
      += Group(Id.auto, name, department, courseNumber))

  override def find(id: Id[Group]): Future[Option[Group]] =
    db.run(groups.filter(_.id === id).result.headOption)

  override def list(): Future[Seq[Group]] =
    db.run(groups.result)

  override def listGroupStudentIds(groupId: Id[Group]): Future[Seq[Id[User]]] =
    db.run(users.filter(_.groupId === groupId).map(_.id).result)
}
