package hw.ppposd.lms.auth

import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.Controller
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Json, Reads}

import scala.concurrent.{ExecutionContext, Future}

class AuthController(authRepo: AuthRepository) extends Controller {
  private val sessionCookieName = "SESSION"
  import AuthController._
  import AuthUtils._

  def route(implicit ec: ExecutionContext): Route = pathPrefix("auth") { concat (
    path("login") {
      post { entity(as[LoginEntity]) { entity => onSuccess(login(entity)) {
        case Some(session) => setCookie(HttpCookie(sessionCookieName, value = session)) { complete("success") }
        case None => complete(401, "login failed")
      }}}
    },
    path("register") {
      post { entity(as[RegisterEntity]) { entity => onSuccess(register(entity)) {
        if (_) complete("success") else complete(401, "invalid verification")
      }}}
    },
    path("change-password") {
      userSession { userId =>
        post { entity(as[ChangePasswordEntity]) { entity => onSuccess(changePassword(userId, entity)) {
          if (_) complete("success") else complete(401, "invalid passwords specified")
        }}}
      }
    },
  )}

  def userSession(innerRoute: Id[User] => Route)(implicit ec: ExecutionContext): Route =
    cookie(sessionCookieName) { session =>
      onSuccess(sessionToUserId(session.value)) {
        case Some(userId) => innerRoute(userId)
        case None => complete(401, "invalid session")
      }
    }

  private def sessionToUserId(session: String): Future[Option[Id[User]]] = {
    authRepo.findUserIdBySession(session)
  }

  private def login(entity: LoginEntity)(implicit ec: ExecutionContext): Future[Option[String]] = {
    val passwordHash = hashPassword(entity.password)
    val userIdOptFuture = authRepo.findUserIdByAuthPair(entity.email, passwordHash)
    userIdOptFuture.flatMap {
      case Some(userId) => authRepo.createSession(userId).map(Some(_))
      case None => Future.successful(None)
    }
  }

  private def register(entity: RegisterEntity)(implicit ec: ExecutionContext): Future[Boolean] = {
    if (isPasswordStrongEnough(entity.password) && isEmailValid(entity.email)) {
      val passwordHash = hashPassword(entity.password)
      val userIdOptFuture = authRepo.findUserIdByVerification(entity.verificationCode)
      userIdOptFuture.flatMap {
        case Some(userId) =>
          authRepo.setAuthPair(userId, entity.email, passwordHash)
            .flatMap(_ => authRepo.destroyVerification(entity.verificationCode))
            .map(_ => true)
        case None => Future.successful(false)
      }
    } else {
      Future.successful(false)
    }
  }

  private def changePassword(userId: Id[User], entity: ChangePasswordEntity)
                            (implicit ec: ExecutionContext): Future[Boolean] = {
    if (isPasswordStrongEnough(entity.newPassword)) {
      val oldPasswordHash = hashPassword(entity.oldPassword)
      val newPasswordHash = hashPassword(entity.newPassword)
      authRepo.getPasswordHash(userId).flatMap {
        case x if x == oldPasswordHash =>
          authRepo.setPasswordHash(userId, newPasswordHash).map(_ => true)
        case _ => Future.successful(false)
      }
    } else {
      Future.successful(false)
    }
  }
}

object AuthController extends PlayJsonSupport {
  final case class LoginEntity(email: String, password: String)
  implicit val loginFormat: Reads[LoginEntity] = Json.reads[LoginEntity]

  final case class RegisterEntity(verificationCode: String, email: String, password: String)
  implicit val registerFormat: Reads[RegisterEntity] = Json.reads[RegisterEntity]

  final case class ChangePasswordEntity(oldPassword: String, newPassword: String)
  implicit val changePasswordFormat: Reads[ChangePasswordEntity] = Json.reads[ChangePasswordEntity]
}

