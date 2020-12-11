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
  import AuthController._
  def route(implicit ec: ExecutionContext): Route = concat (
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
      post { entity(as[ChangePasswordEntity]) { entity => changePassword(entity) } }
    },
  )

  def sessionToUserId(session: String): Future[Option[Id[User]]] = {
    authRepo.findUserIdBySession(session)
  }

  private def login(entity: LoginEntity)(implicit ec: ExecutionContext): Future[Option[String]] = {
    val passwordHash = AuthUtils.hashPassword(entity.password)
    val userIdOptFuture = authRepo.findUserIdByAuth(entity.username, passwordHash)
    userIdOptFuture.flatMap {
      case Some(userId) => authRepo.createSession(userId).map(Some(_))
      case None => Future.successful(None)
    }
  }

  private def register(entity: RegisterEntity): Future[Unit] = {
    ???
  }

  private def changePassword(entity: ChangePasswordEntity): Future[Unit] = {
    ???
  }
}

object AuthController extends PlayJsonSupport {
  final case class LoginEntity(username: String, password: String)
  implicit val loginFormat: Reads[LoginEntity] = Json.reads[LoginEntity]

  final case class RegisterEntity(verificationCode: String, username: String, password: String)
  implicit val registerFormat: Reads[RegisterEntity] = Json.reads[RegisterEntity]

  final case class ChangePasswordEntity(oldPassword: String, newPassword: String)
  implicit val changePasswordFormat: Reads[ChangePasswordEntity] = Json.reads[ChangePasswordEntity]
}

