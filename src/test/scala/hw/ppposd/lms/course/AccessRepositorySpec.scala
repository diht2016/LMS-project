package hw.ppposd.lms.course

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase

class AccessRepositorySpec extends DatabaseSpecBase {
  import AccessRepositorySpec._

  "isCourseTutor" should "return true if user is a course tutor" in new TestWiring {
    private val userId = testData.courseTutors.find(_.courseId == courseId).get.studentId
    whenReady(repo.isCourseTutor(userId, courseId)) {
      _ shouldBe true
    }
  }

  it should "return false if user is not a course tutor" in new TestWiring {
    private val userId = testData.courseTeachers.find(_.courseId == courseId).get.teacherId
    whenReady(repo.isCourseTutor(userId, courseId)) {
      _ shouldBe false
    }
  }

  "isCourseTeacher" should "return true if user is a course teacher" in new TestWiring {
    private val userId = testData.courseTeachers.find(_.courseId == courseId).get.teacherId
    whenReady(repo.isCourseTeacher(userId, courseId)) {
      _ shouldBe true
    }
  }

  it should "return false if user is not a course teacher" in new TestWiring {
    private val userId = testData.courseTutors.find(_.courseId == courseId).get.studentId
    whenReady(repo.isCourseTeacher(userId, courseId)) {
      _ shouldBe false
    }
  }

  "isCourseStudent" should "return true if user is a course student" in new TestWiring {
    private val userId = testData.users(1).id
    whenReady(repo.isCourseStudent(userId, courseId)) {
      _ shouldBe true
    }
  }

  it should "return false if user is not a course student" in new TestWiring {
    private val userId = testData.users(8).id
    whenReady(repo.isCourseStudent(userId, courseId)) {
      _ shouldBe false
    }
  }

  trait TestWiring {
    val repo: AccessRepository = new AccessRepositoryImpl
  }
}

object AccessRepositorySpec {
  private val courseId = testData.courses(1).id
}
