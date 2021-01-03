package hw.ppposd.lms.course

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
      _.toList should be (coursesData)
    }
  }

  "find" should "return existing course" in new TestWiring {
    whenReady(repo.find(philosophyCourse.id)) {
      _ should be (Some(philosophyCourse))
    }
  }

  trait TestWiring {
    val repo: CourseRepository = new CourseRepositoryImpl
  }
}

