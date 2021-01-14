package hw.ppposd.lms.course

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase
import hw.ppposd.lms.util.Id

class CourseRepositorySpec extends DatabaseSpecBase {

  "list" should "return all courses" in new TestWiring {
    whenReady(repo.list()) {
      _ shouldBe testData.courses
    }
  }

  "find" should "return existing course" in new TestWiring {
    val course = testData.courses.last
    whenReady(repo.find(course.id)) {
      _ shouldBe Some(course)
    }
  }

  "create" should "create new course" in new TestWiring {
    private val newCourse = Course(Id.auto, "temp course", "...")

    whenReady(repo.create(newCourse.name, newCourse.description)) { newId =>
      val newCourseWithId = newCourse.copy(id = newId)
      whenReady(repo.find(newId)) {
        _ shouldBe Some(newCourseWithId)
      }
    }
  }

  trait TestWiring {
    val repo: CourseRepository = new CourseRepositoryImpl
  }
}

