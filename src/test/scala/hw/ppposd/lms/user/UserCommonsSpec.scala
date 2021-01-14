package hw.ppposd.lms.user

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase

import scala.concurrent.ExecutionContext.Implicits.global

class UserCommonsSpec extends DatabaseSpecBase {
  import UserCommonsSpec._

  "getUserGroupId" should "return group id option" in new TestWiring {
    whenReady(repo.getUserGroupId(user.id)) {
      _ shouldBe user.groupId
    }
  }

  "enrichUsers" should "return existing user" in new TestWiring {
    whenReady(repo.enrichUsers(userIds)) {
      _ shouldBe userBriefs
    }
  }

  trait TestWiring {
    val repo: UserCommons = new UserCommonsImpl
  }
}

object UserCommonsSpec {
  private val user = testData.users(6)
  private val users = testData.users.slice(2, 7)
  private val userIds = users.map(_.id)
  private val userBriefs = users.map(u => UserBrief(u.id, u.fullName))
}
