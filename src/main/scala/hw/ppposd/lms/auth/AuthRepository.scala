package hw.ppposd.lms.auth

import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.Future

trait AuthRepository {
  def createSession(userId: Id[User]): Future[String]
  def findUserIdBySession(session: String): Future[Option[Id[User]]]
  def findUserIdByAuth(username: String, passwordHash: String): Future[Option[Id[User]]]
  // def destroySession(session: String): Future[Unit]

  def createVerification(fullName: String): Future[String]
  def findFullNameByVerification(code: String): Future[Option[String]]
}

class AuthRepositoryImpl extends AuthRepository {
  override def createSession(userId: Id[User]): Future[String] = ???

  override def findUserIdBySession(session: String): Future[Option[Id[User]]] = ???

  override def findUserIdByAuth(username: String, passwordHash: String): Future[Option[Id[User]]] = ???

  override def createVerification(fullName: String): Future[String] = ???

  override def findFullNameByVerification(code: String): Future[Option[String]] = ???
}
