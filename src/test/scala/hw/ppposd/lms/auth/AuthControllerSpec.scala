package hw.ppposd.lms.auth

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.StatusCodes
import hw.ppposd.lms.base.RouteSpecBase
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.Future

class AuthControllerSpec extends RouteSpecBase {
  import AuthControllerSpec._

  "userSession" should "provide inner route if session exists" in new TestWiringSession {
    authRepoMock.findUserIdBySession _ expects sampleSession returns
      Future.successful(Some(sampleUserId))

    Get() ~> sessionHeader ~> sessionRoute ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe s"user id = $sampleUserId"
    }
  }

  it should "respond with 401 if session does not exist" in new TestWiringSession {
    authRepoMock.findUserIdBySession _ expects sampleSession returns
      Future.successful(None)

    Get() ~> sessionHeader ~> sessionRoute ~> check {
      status shouldBe StatusCodes.Unauthorized
      responseAs[String] shouldBe """{"error":"invalid session"}"""
    }
  }

  it should "respond with 401 if session header is not set" in new TestWiringSession {
    (authRepoMock.findUserIdBySession _ expects *).never()

    Get() ~> sessionRoute ~> check {
      status shouldBe StatusCodes.Unauthorized
      responseAs[String] shouldBe """{"error":"session header is not provided"}"""
    }
  }

  "login" should "create session and set header if credentials match" in new TestWiring {
    (authRepoMock.findUserIdByAuthPair _).expects(sampleEmail, sampleHash) returns
      Future.successful(Some(sampleUserId))
    authRepoMock.createSession _ expects sampleUserId returns
      Future.successful(sampleSession)

    PostJson("/login", sampleLoginEntity) ~> controller.route ~> check {
      status shouldBe StatusCodes.OK
      header(sessionHeaderName) shouldBe Some(sessionHeader)
      responseAs[String] shouldBe okResponse
    }
  }

  it should "respond with 401 if credentials do not match" in new TestWiring {
    (authRepoMock.findUserIdByAuthPair _).expects(sampleEmail, sampleHash) returns
      Future.successful(None)
    (authRepoMock.createSession _ expects *).never()

    PostJson("/login", sampleLoginEntity) ~> controller.route ~> check {
      status shouldBe StatusCodes.Unauthorized
      header(sessionHeaderName) shouldBe None
      responseAs[String] shouldBe """{"error":"login failed"}"""
    }
  }

  "register" should "update user data and destroy verification if all is correct" in new TestWiring {
    authRepoMock.findUserIdByVerification _ expects sampleCode returns
      Future.successful(Some(sampleUserId))
    authRepoMock.destroyVerification _ expects sampleCode returns
      Future.successful(1)
    (authRepoMock.setAuthPair _).expects(sampleUserId, sampleEmail, sampleHash) returns
      Future.successful(1)

    PostJson("/register", sampleRegisterEntity) ~> controller.route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  it should "respond with 401 if verification code does not exist" in new TestWiring {
    authRepoMock.findUserIdByVerification _ expects sampleCode returns
      Future.successful(None)

    PostJson("/register", sampleRegisterEntity) ~> controller.route ~> check {
      status shouldBe StatusCodes.Unauthorized
      responseAs[String] shouldBe """{"error":"invalid verification code"}"""
    }
  }

  it should "respond with 400 if password is weak" in new TestWiring {
    (authRepoMock.findUserIdByVerification _ expects sampleCode).never()

    PostJson("/register", sampleRegisterEntity.copy(password = sampleWeakPassword)) ~>
      controller.route ~> check {
      status shouldBe StatusCodes.BadRequest
      responseAs[String] shouldBe """{"error":"password is too weak"}"""
    }
  }

  it should "respond with 400 if email is incorrect" in new TestWiring {
    (authRepoMock.findUserIdByVerification _ expects sampleCode).never()

    PostJson("/register", sampleRegisterEntity.copy(email = sampleBadEmail)) ~>
      controller.route ~> check {
      status shouldBe StatusCodes.BadRequest
      responseAs[String] shouldBe """{"error":"email is not valid"}"""
    }
  }

  "changePassword" should "update password if all is correct" in new TestWiring {
    authRepoMock.findUserIdBySession _ expects sampleSession returns
      Future.successful(Some(sampleUserId))
    authRepoMock.getPasswordHash _ expects sampleUserId returns
      Future.successful(sampleHash)
    (authRepoMock.setPasswordHash _).expects(sampleUserId, sampleNewHash) returns
      Future.successful(1)

    PostJson("/change-password", samplePasswordEntity) ~>
      sessionHeader ~> controller.route ~> check {
      status shouldBe StatusCodes.OK
      responseAs[String] shouldBe okResponse
    }
  }

  it should "respond with 401 if old password is wrong" in new TestWiring {
    authRepoMock.findUserIdBySession _ expects sampleSession returns
      Future.successful(Some(sampleUserId))
    authRepoMock.getPasswordHash _ expects sampleUserId returns
      Future.successful(sampleHash)

    PostJson("/change-password", samplePasswordEntity.copy(oldPassword = sampleNewPassword)) ~>
      sessionHeader ~> controller.route ~> check {
      status shouldBe StatusCodes.Unauthorized
      responseAs[String] shouldBe """{"error":"old passwords do not match"}"""
    }
  }

  it should "respond with 400 if new password is weak" in new TestWiring {
    authRepoMock.findUserIdBySession _ expects sampleSession returns
      Future.successful(Some(sampleUserId))

    PostJson("/change-password", samplePasswordEntity.copy(newPassword = sampleWeakPassword)) ~>
      sessionHeader ~> controller.route ~> check {
      status shouldBe StatusCodes.BadRequest
      responseAs[String] shouldBe """{"error":"new password is too weak"}"""
    }
  }

  it should "respond with 401 if user is not logged in" in new TestWiring {
    (authRepoMock.findUserIdBySession _ expects *).never()

    PostJson("/change-password", samplePasswordEntity) ~> controller.route ~> check {
      status shouldBe StatusCodes.Unauthorized
      responseAs[String] shouldBe """{"error":"session header is not provided"}"""
    }
  }

  private trait TestWiring {
    protected val authRepoMock = mock[AuthRepository]
    protected val controller = new AuthController(authRepoMock)
  }

  private trait TestWiringSession extends TestWiring {
    import akka.http.scaladsl.server.Route
    import akka.http.scaladsl.server.Directives.complete

    protected val sessionRoute: Route = controller.userSession { userId =>
      complete(s"user id = $userId")
    }
  }
}

object AuthControllerSpec {
  import hw.ppposd.lms.auth.AuthController._

  private val sessionHeaderName = "Session"
  private val sampleSession = "sample session"
  private val sampleUserId = new Id[User](123)
  private val sessionHeader: RawHeader = RawHeader(sessionHeaderName, sampleSession)

  private val sampleCode = "sample verification code"

  private val sampleEmail = "example@example.com"
  private val samplePassword = "https://xkcd.com/936/"
  private val sampleHash: String = AuthUtils.hashPassword(samplePassword)
  private val sampleNewPassword = "new_password_is_still_strong"
  private val sampleNewHash: String = AuthUtils.hashPassword(sampleNewPassword)

  private val sampleBadEmail = "example@example"
  private val sampleWeakPassword = "qwerty123"

  private val sampleLoginEntity = LoginEntity(sampleEmail, samplePassword)
  private val sampleRegisterEntity = RegisterEntity(sampleCode, sampleEmail, samplePassword)
  private val samplePasswordEntity = ChangePasswordEntity(samplePassword, sampleNewPassword)
}
