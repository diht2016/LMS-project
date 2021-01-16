package hw.ppposd.lms.course.tutor

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase
import hw.ppposd.lms.course.{AccessRepository, AccessRepositoryImpl}

class TutorRepositorySpec extends DatabaseSpecBase {
  import TutorRepositorySpec._

  "add" should "add tutor to course" in new TestWiring {
    whenReady(repo.add(courseId, tutorId)) {
      _ shouldBe 1
    }
    whenReady(accessRepo.isCourseTutor(tutorId, courseId)) {
      _ shouldBe true
    }
  }

  "delete" should "delete tutor from course" in new TestWiring {
    whenReady(repo.delete(courseId, tutorId)) {
      _ shouldBe 1
    }
    whenReady(accessRepo.isCourseTutor(tutorId, courseId)) {
      _ shouldBe false
    }
  }

  "listCourseTutorIds" should "return list of course tutor ids" in new TestWiring {
    whenReady(repo.listCourseTutorIds(courseId)) {
      _ shouldBe testData.courseTutors
        .filter(_.courseId == courseId).map(_.studentId)
    }
  }

  trait TestWiring {
    val repo: TutorRepository = new TutorRepositoryImpl
    val accessRepo: AccessRepository = new AccessRepositoryImpl
  }
}

object TutorRepositorySpec {
  private val courseId = testData.courses(1).id
  private val tutorId = testData.users(1).id
}
