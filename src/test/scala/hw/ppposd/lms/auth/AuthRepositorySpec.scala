package hw.ppposd.lms.auth

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase

import scala.concurrent.ExecutionContext.Implicits.global

class AuthRepositorySpec extends DatabaseSpecBase {
  import AuthRepositorySpec._

  "findUserIdByAuthPair" should "find user by email and password hash" in new TestWiring {
    whenReady(repo.findUserIdByAuthPair(sampleUser.email, sampleUser.passwordHash)) {
      _ shouldBe Some(sampleUserId)
    }
  }

  "getPasswordHash" should "get user's password hash" in new TestWiring {
    whenReady(repo.getPasswordHash(sampleUserId)) {
      _ shouldBe sampleUser.passwordHash
    }
  }

  "createSession" should "generate new session" in new TestWiring {
    whenReady(repo.createSession(sampleUserId)) { session =>
      whenReady(repo.findUserIdBySession(session)) {
        _ shouldBe Some(sampleUserId)
      }
    }
  }

  "destroySession" should "delete session" in new TestWiring {
    whenReady(repo.createSession(sampleUserId)) { session =>
      whenReady(repo.findUserIdBySession(session)) {
        _ shouldBe Some(sampleUserId)
      }
      whenReady(repo.destroySession(session)) {
        _ shouldBe 1
      }
      whenReady(repo.findUserIdBySession(session)) {
        _ shouldBe None
      }
    }
  }

  "createVerification" should "generate new verification code" in new TestWiring {
    whenReady(repo.createVerification(sampleUserId)) { code =>
      whenReady(repo.findUserIdByVerification(code)) {
        _ shouldBe Some(sampleUserId)
      }
    }
  }

  "destroyVerification" should "delete verification" in new TestWiring {
    whenReady(repo.createVerification(sampleUserId)) { code =>
      whenReady(repo.findUserIdByVerification(code)) {
        _ shouldBe Some(sampleUserId)
      }
      whenReady(repo.destroyVerification(code)) {
        _ shouldBe 1
      }
      whenReady(repo.findUserIdByVerification(code)) {
        _ shouldBe None
      }
    }
  }

  "setAuthPair" should "change email and password hash" in new TestWiring {
    whenReady(repo.setAuthPair(sampleUserId, sampleEmail, samplePasswordHash)) { result =>
      result shouldBe 1
      whenReady(repo.findUserIdByAuthPair(sampleEmail, samplePasswordHash)) {
        _ shouldBe Some(sampleUserId)
      }
    }
  }

  "setPasswordHash" should "update user's password hash" in new TestWiring {
    whenReady(repo.setPasswordHash(sampleUserId, samplePasswordHash)) { result =>
      result shouldBe 1
      whenReady(repo.getPasswordHash(sampleUserId)) {
        _ shouldBe samplePasswordHash
      }
    }
  }

  trait TestWiring {
    val repo: AuthRepository = new AuthRepositoryImpl
  }
}

object AuthRepositorySpec {
  private val sampleUser = testData.users(5)
  private val sampleUserId = sampleUser.id
  private val sampleEmail = "new.mail@example.com"
  private val samplePasswordHash = "sample password hash"
}

