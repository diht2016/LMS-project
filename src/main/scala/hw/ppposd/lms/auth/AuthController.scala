package hw.ppposd.lms.auth

import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.{Directive1, Route}
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.Controller
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Json, Reads}

import scala.concurrent.{ExecutionContext, Future}

class AuthController(authRepo: AuthRepository) extends Controller {
  import AuthController._
  def route(implicit ec: ExecutionContext): Route = pathPrefix("auth") { concat (
    path("login") {
      post { entity(as[LoginEntity]) { entity => onSuccess(login(entity)) {
        case Some(session) => setCookie(HttpCookie("SESSION", value = session)) { complete("success") }
        case None => complete(401, "login failed")
      }}}
    },
    path("register") {
      post { entity(as[RegisterEntity]) { entity => register(entity) } }
    },
    path("change-password") {
      userSession { userId =>
        post { entity(as[ChangePasswordEntity]) { entity => changePassword(userId, entity) } }
      }
    },
  )}

  def userSession(innerRoute: Id[User] => Route)(implicit ec: ExecutionContext): Route =
    cookie("SESSION") { session =>
      onSuccess(sessionToUserId(session.value)) {
        case Some(userId) => innerRoute(userId)
        case None => complete(401, "invalid session")
      }
    }

  private def sessionToUserId(session: String): Future[Option[Id[User]]] = {
    authRepo.findUserIdBySession(session)
  }

  private def login(entity: LoginEntity)(implicit ec: ExecutionContext): Future[Option[String]] = {
    val passwordHash = AuthUtils.hashPassword(entity.password)
    val userIdOptFuture = authRepo.findUserIdByAuthPair(entity.email, passwordHash)
    userIdOptFuture.flatMap {
      case Some(userId) => authRepo.createSession(userId).map(Some(_))
      case None => Future.successful(None)
    }
  }

  private def register(entity: RegisterEntity): Future[Unit] = {
    ???
  }

  private def changePassword(userId: Id[User], entity: ChangePasswordEntity): Future[Unit] = {
    ???
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

