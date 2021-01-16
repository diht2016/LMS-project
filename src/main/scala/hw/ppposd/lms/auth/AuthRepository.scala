package hw.ppposd.lms.auth

import slick.jdbc.H2Profile.api._
import hw.ppposd.lms.auth.AuthUtils._
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

trait AuthRepository {
  def createSession(userId: Id[User]): Future[String]
  def findUserIdBySession(token: String): Future[Option[Id[User]]]
  def findUserIdByAuthPair(email: String, passwordHash: String): Future[Option[Id[User]]]
  def destroySession(token: String): Future[Int]

  def createVerification(userId: Id[User]): Future[String]
  def findUserIdByVerification(code: String): Future[Option[Id[User]]]
  def destroyVerification(code: String): Future[Int]

  def setAuthPair(userId: Id[User], email: String, passwordHash: String): Future[Int]
  def getPasswordHash(userId: Id[User]): Future[String]
  def setPasswordHash(userId: Id[User], passwordHash: String): Future[Int]
}

class AuthRepositoryImpl(implicit db: Database, ec: ExecutionContext) extends AuthRepository {
  import hw.ppposd.lms.Schema._

  override def createSession(userId: Id[User]): Future[String] = {
    val session = Session(randomSessionToken, userId)
    db.run(sessions += session).map(_ => session.token)
  }

  override def findUserIdBySession(token: String): Future[Option[Id[User]]] =
    db.run(sessions.filter(_.token === token).map(_.userId).result.headOption)

  override def findUserIdByAuthPair(email: String, passwordHash: String): Future[Option[Id[User]]] =
    db.run(users.filter(u => u.email === email && u.passwordHash === passwordHash)
      .map(_.id).result.headOption)

  override def destroySession(token: String): Future[Int] =
    db.run(sessions.filter(_.token === token).delete)

  override def createVerification(userId: Id[User]): Future[String] = {
    val verification = Verification(randomVerificationCode, userId)
    db.run(verifications += verification).map(_ => verification.code)
  }

  override def findUserIdByVerification(code: String): Future[Option[Id[User]]] =
    db.run(verifications.filter(_.code === code).map(_.userId).result.headOption)

  override def destroyVerification(code: String): Future[Int] =
    db.run(verifications.filter(_.code === code).delete)

  override def setAuthPair(userId: Id[User], email: String, passwordHash: String): Future[Int] =
    db.run(users.filter(_.id === userId).map(x => (x.email, x.passwordHash))
      .update(email, passwordHash))

  override def getPasswordHash(userId: Id[User]): Future[String] =
    db.run(users.filter(_.id === userId).map(_.passwordHash).result.head)

  override def setPasswordHash(userId: Id[User], passwordHash: String): Future[Int] =
    db.run(users.filter(_.id === userId).map(_.passwordHash)
      .update(passwordHash))
}
