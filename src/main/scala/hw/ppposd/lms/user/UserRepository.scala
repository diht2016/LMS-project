package hw.ppposd.lms.user

import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.personaldata.PersonalData
import hw.ppposd.lms.user.studentdata.StudentData
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait UserRepository {
  def create(fullName: String, groupId: Option[Id[Group]]): Future[Id[User]]
  def createRegistered(fullName: String,
                       email: String,
                       passwordHash: String,
                       groupId: Option[Id[Group]]): Future[Id[User]]
  def find(id: Id[User]): Future[Option[User]]
  def list(): Future[Seq[User]]

  def findPersonalData(id: Id[User]): Future[Option[PersonalData]]
  def findStudentData(id: Id[User]): Future[Option[StudentData]]
  def setPersonalData(data: PersonalData): Future[Int]
}

class UserRepositoryImpl(implicit db: Database) extends UserRepository {
  import hw.ppposd.lms.Schema._

  override def create(fullName: String, groupId: Option[Id[Group]]): Future[Id[User]] =
    db.run((users returning users.map(_.id))
      += User(Id.auto, fullName, "", "", groupId))

  override def createRegistered(fullName: String,
                                email: String,
                                passwordHash: String,
                                groupId: Option[Id[Group]]): Future[Id[User]] =
    db.run((users returning users.map(_.id))
      += User(Id.auto, fullName, email, passwordHash, groupId))

  override def find(id: Id[User]): Future[Option[User]] =
    db.run(users.filter(_.id === id).result.headOption)

  override def list(): Future[Seq[User]] =
    db.run(users.result)

  override def findPersonalData(id: Id[User]): Future[Option[PersonalData]] =
    db.run(personalData.filter(_.userId === id).result.headOption)

  override def findStudentData(id: Id[User]): Future[Option[StudentData]] =
    db.run(studentData.filter(_.studentId === id).result.headOption)

  override def setPersonalData(data: PersonalData): Future[Int] =
    db.run(personalData.update(data))
}
