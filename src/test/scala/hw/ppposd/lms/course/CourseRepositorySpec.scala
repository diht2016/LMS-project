package hw.ppposd.lms.course

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase
import hw.ppposd.lms.util.Id

class CourseRepositorySpec extends DatabaseSpecBase {

  "create" should "create new course" in new TestWiring {
    private val newCourse = Course(Id.auto, "temp course", "...")

    whenReady(repo.create(newCourse.name, newCourse.description), oneSecond) { newId =>
      val newCourseWithId = newCourse.copy(id = newId)
      whenReady(repo.find(newId), oneSecond) {
        _ should be (Some(newCourseWithId))
      }
    }
  }

  "list" should "return all courses" in new TestWiring {
    whenReady(repo.list()) {
      _.toList should be (testData.courses)
    }
  }

  "find" should "return existing course" in new TestWiring {
    whenReady(repo.find(testData.courses(0).id)) {
      _ should be (Some(testData.courses(0)))
    }
  }

  trait TestWiring {
    val repo: CourseRepository = new CourseRepositoryImpl
  }

}

