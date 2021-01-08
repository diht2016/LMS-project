package hw.ppposd.lms.auth

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hw.ppposd.lms.Controller
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Format, Json}

import scala.concurrent.{ExecutionContext, Future}

class AuthController(authRepo: AuthRepository)(implicit ec: ExecutionContext) extends Controller {
  private val sessionHeaderName = "Session"
  import AuthController._
  import AuthUtils._

  def route: Route = concat(
    (path("login") & post & entity(as[LoginEntity])) { entity =>
      futureToResponse(
        login(entity),
        (session: String) => respondWithHeaders(RawHeader(sessionHeaderName, session)) {
          successResponse
        }
      )
    },
    (path("register") & post & entity(as[RegisterEntity])) { entity =>
      register(entity)
    },
    (path("change-password") & post & entity(as[ChangePasswordEntity])) { entity =>
      userSession { userId =>
        changePassword(userId, entity)
      }
    },
  )

  def userSession(innerRoute: Id[User] => Route): Route =
    headerValueByName(sessionHeaderName) { session =>
      onSuccess(authRepo.findUserIdBySession(session)) {
        case Some(userId) => innerRoute(userId)
        case None => ApiError(401, "invalid session")
      }
    } ~ ApiError(401, "session header is not provided")

  private def login(entity: LoginEntity): Future[String] = {
    val passwordHash = hashPassword(entity.password)
    val userIdOptFuture = authRepo.findUserIdByAuthPair(entity.email, passwordHash)
    userIdOptFuture.flatMap {
      case Some(userId) => authRepo.createSession(userId)
      case None => ApiError(401, "login failed")
    }
  }

  private def register(entity: RegisterEntity): Future[Unit] = {
    if (!isPasswordStrongEnough(entity.password)) {
      ApiError(400, "password is too weak")
    } else if (!isEmailValid(entity.email)) {
      ApiError(400, "email is not valid")
    } else {
      val passwordHash = hashPassword(entity.password)
      val userIdOptFuture = authRepo.findUserIdByVerification(entity.verificationCode)
      userIdOptFuture.flatMap {
        case Some(userId) =>
          authRepo.setAuthPair(userId, entity.email, passwordHash)
            .flatMap(assertSingleUpdate)
            .flatMap(_ => authRepo.destroyVerification(entity.verificationCode))
            .flatMap(assertSingleUpdate)
        case None => ApiError(401, "invalid verification code")
      }
    }
  }

  private def changePassword(userId: Id[User], entity: ChangePasswordEntity): Future[Unit] = {
    if (!isPasswordStrongEnough(entity.newPassword)) {
      ApiError(400, "new password is too weak")
    } else {
      val oldPasswordHash = hashPassword(entity.oldPassword)
      val newPasswordHash = hashPassword(entity.newPassword)
      authRepo.getPasswordHash(userId).flatMap {
        case dbPasswordHash if dbPasswordHash == oldPasswordHash =>
          authRepo.setPasswordHash(userId, newPasswordHash)
            .flatMap(assertSingleUpdate)
        case _ => ApiError(401, "old passwords do not match")
      }
    }
  }
}

object AuthController extends PlayJsonSupport {
  final case class LoginEntity(email: String, password: String)
  implicit val loginFormat: Format[LoginEntity] = Json.format[LoginEntity]

  final case class RegisterEntity(verificationCode: String, email: String, password: String)
  implicit val registerFormat: Format[RegisterEntity] = Json.format[RegisterEntity]

  final case class ChangePasswordEntity(oldPassword: String, newPassword: String)
  implicit val changePasswordFormat: Format[ChangePasswordEntity] = Json.format[ChangePasswordEntity]
}
