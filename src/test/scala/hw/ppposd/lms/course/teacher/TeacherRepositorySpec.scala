package hw.ppposd.lms.course.teacher

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase
import hw.ppposd.lms.course.{AccessRepository, AccessRepositoryImpl}

class TeacherRepositorySpec extends DatabaseSpecBase {
  import TeacherRepositorySpec._

  "add" should "add teacher to course" in new TestWiring {
    whenReady(repo.add(courseId, teacherId)) {
      _ shouldBe 1
    }
    whenReady(accessRepo.isCourseTeacher(teacherId, courseId)) {
      _ shouldBe true
    }
  }

  "delete" should "delete teacher from course" in new TestWiring {
    whenReady(repo.delete(courseId, teacherId)) {
      _ shouldBe 1
    }
    whenReady(accessRepo.isCourseTeacher(teacherId, courseId)) {
      _ shouldBe false
    }
  }

  "listCourseTeacherIds" should "return list of course teacher ids" in new TestWiring {
    whenReady(repo.listCourseTeacherIds(courseId)) {
      _ shouldBe testData.courseTeachers
        .filter(_.courseId == courseId).map(_.teacherId)
    }
  }

  trait TestWiring {
    val repo: TeacherRepository = new TeacherRepositoryImpl
    val accessRepo: AccessRepository = new AccessRepositoryImpl
  }
}

object TeacherRepositorySpec {
  private val courseId = testData.courses(1).id
  private val teacherId = testData.users(12).id
}
