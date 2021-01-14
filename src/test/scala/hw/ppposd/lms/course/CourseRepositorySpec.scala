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
    private val course = testData.courses.last
    whenReady(repo.find(course.id)) {
      _ shouldBe Some(course)
    }
  }

  "listTeacherCourseIds" should "return all teacher's course ids" in new TestWiring {
    private val userId = testData.users(11).id
    whenReady(repo.listTeacherCourseIds(userId)) {
      _ shouldBe testData.courseTeachers
        .filter(_.teacherId == userId)
        .map(_.courseId)
    }
  }

  "listGroupCourseIds" should "return all group's course ids" in new TestWiring {
    private val groupId = testData.groups(1).id
    whenReady(repo.listGroupCourseIds(groupId)) {
      _ shouldBe testData.groupCourses
        .filter(_.groupId == groupId)
        .map(_.courseId)
    }
  }

  "enrichCourses" should "enrich course ids with other course data" in new TestWiring {
    private val courses = testData.courses
    private val courseIds = courses.map(_.id)
    whenReady(repo.enrichCourses(courseIds)) {
      _ shouldBe courses
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
