package hw.ppposd.lms.auth

import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.Future

trait AuthRepository {
  def createSession(userId: Id[User]): Future[String]
  def findUserIdBySession(session: String): Future[Option[Id[User]]]
  def findUserIdByAuthPair(email: String, passwordHash: String): Future[Option[Id[User]]]
  def destroySession(session: String): Future[Unit]

  def createVerification(fullName: String): Future[String]
  def findUserIdByVerification(code: String): Future[Option[Id[User]]]
  def destroyVerification(session: String): Future[Unit]

  def setAuthPair(userId: Id[User], email: String, passwordHash: String): Future[Unit]
  def getPasswordHash(userId: Id[User]): Future[String]
  def setPasswordHash(userId: Id[User], passwordHash: String): Future[Unit]
}

class AuthRepositoryImpl extends AuthRepository {
  override def createSession(userId: Id[User]): Future[String] = ???

  override def findUserIdBySession(session: String): Future[Option[Id[User]]] = ???

  override def findUserIdByAuthPair(email: String, passwordHash: String): Future[Option[Id[User]]] = ???

  override def destroySession(session: String): Future[Unit] = ???

  override def createVerification(fullName: String): Future[String] = ???

  override def findUserIdByVerification(code: String): Future[Option[Id[User]]] = ???

  override def destroyVerification(session: String): Future[Unit] = ???

  override def setAuthPair(userId: Id[User], email: String, passwordHash: String): Future[Unit] = ???

  override def getPasswordHash(userId: Id[User]): Future[String] = ???

  override def setPasswordHash(userId: Id[User], passwordHash: String): Future[Unit] = ???
}
