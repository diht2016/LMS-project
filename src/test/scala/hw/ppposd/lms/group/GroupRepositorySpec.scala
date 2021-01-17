package hw.ppposd.lms.group

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase
import hw.ppposd.lms.util.Id

class GroupRepositorySpec extends DatabaseSpecBase {

  "list" should "return all courses" in new TestWiring {
    whenReady(repo.list()) {
      _ shouldBe testData.groups
    }
  }

  "find" should "return existing course" in new TestWiring {
    private val group = testData.groups.last
    whenReady(repo.find(group.id)) {
      _ shouldBe Some(group)
    }
  }

  "listGroupStudentIds" should "list ids of group students" in new TestWiring {
    private val group = testData.groups(1)
    whenReady(repo.listGroupStudentIds(group.id)) {
      _ shouldBe testData.users
        .filter(_.groupId.contains(group.id))
        .map(_.id)
    }
  }

  "listCourseGroups" should "list ids of group students" in new TestWiring {
    private val course = testData.courses(1)
    whenReady(repo.listCourseGroups(course.id)) {
      _ shouldBe testData.groups.slice(0, 2)
    }
  }

  "create" should "create new course" in new TestWiring {
    private val newGroup = Group(Id.auto, "new group", "department", 2020)

    whenReady(repo.create(newGroup.name, newGroup.department, newGroup.courseNumber)) { newId =>
      val newGroupWithId = newGroup.copy(id = newId)
      whenReady(repo.find(newId)) {
        _ shouldBe Some(newGroupWithId)
      }
    }
  }

  trait TestWiring {
    val repo: GroupRepository = new GroupRepositoryImpl
  }
}
