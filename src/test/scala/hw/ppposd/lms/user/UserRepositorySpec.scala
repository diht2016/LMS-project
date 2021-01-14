package hw.ppposd.lms.user

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.util.Id

class UserRepositorySpec extends DatabaseSpecBase {
  import UserRepositorySpec._

  "list" should "return all users" in new TestWiring {
    whenReady(repo.list()) {
      _.map(_.fullName) shouldBe testData.users.map(_.fullName)
    }
  }

  "find" should "return existing user" in new TestWiring {
    whenReady(repo.find(user.id)) {
      _ shouldBe Some(user)
    }
  }

  "findPersonalData" should "return personal data of existing user" in new TestWiring {
    whenReady(repo.findPersonalData(user.id)) {
      _ shouldBe Some(userPersonalData)
    }
  }

  "findStudentData" should "return student data of existing user" in new TestWiring {
    whenReady(repo.findStudentData(user.id)) {
      _ shouldBe Some(userStudentData)
    }
  }

  "setPersonalData" should "update personal data of existing user" in new TestWiring {
    whenReady(repo.setPersonalData(newPersonalData)) { result =>
      result shouldBe 1
      whenReady(repo.findPersonalData(user.id)) {
        _ shouldBe Some(newPersonalData)
      }
    }
  }

  "create" should "create new user" in new TestWiring {
    whenReady(repo.create(newUser.fullName, newUser.groupId)) { newId =>
      val newUserWithId = newUser.copy(id = newId, email = "", passwordHash = "")
      whenReady(repo.find(newId)) {
        _ shouldBe Some(newUserWithId)
      }
    }
  }

  "createRegistered" should "create new user" in new TestWiring {
    whenReady(repo.createRegistered(newUser.fullName, newUser.email,
      newUser.passwordHash, newUser.groupId)) { newId =>
      val newUserWithId = newUser.copy(id = newId)
      whenReady(repo.find(newId)) {
        _ shouldBe Some(newUserWithId)
      }
    }
  }

  trait TestWiring {
    val repo: UserRepository = new UserRepositoryImpl
  }
}

object UserRepositorySpec {
  private val groupId = Some(new Id[Group](123))
  private val newUser = User(Id.auto, "name", "email", "passwordHash", groupId)
  private val user = testData.users(6)
  private val userPersonalData = testData.personalData(6)
  private val userStudentData = testData.studentData(6)
  private val newPersonalData = userPersonalData.copy(city = Some("new city"))
}
