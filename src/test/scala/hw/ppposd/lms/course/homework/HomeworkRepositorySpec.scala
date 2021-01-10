package hw.ppposd.lms.course.homework

import java.sql.Timestamp

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase
import hw.ppposd.lms.util.Id

class HomeworkRepositorySpec extends DatabaseSpecBase {

  "find" should "find a homework with a given id" in new TestWiring {
    whenReady(repo.find(testData.homeworks(2).homeworkId)) {
      _ shouldBe Some(testData.homeworks(2))
    }
  }

  "listAll" should "return all homeworks for the course" in new TestWiring {
    whenReady(repo.listAll(testData.courses(0).id)) {
      _.toList shouldBe testData.homeworks.slice(0, 3)
    }
  }

  "listAvailable" should "return homeworks with startDate not greater than current date" in new TestWiring {
    whenReady(repo.listAvailable(testData.courses(1).id)) {
      _.toList shouldBe List(testData.homeworks(5))
    }
  }

  "add" should "create a new homework" in new TestWiring {
    val hw = Homework(Id.auto,
      testData.courses(1).id,
      "Z.Freud", "Make a report",
      Timestamp.valueOf("2020-11-1 10:00:00"),
      Timestamp.valueOf("2020-11-7 10:00:00"))

    whenReady(repo.add(hw.courseId, hw.name, hw.description, hw.startDate, hw.deadlineDate)) { newId =>
      val newHw = hw.copy(homeworkId = newId)
      whenReady(repo.find(newId)) { res =>
        res shouldBe Some(newHw)
      }
    }
  }

  "edit" should "update fields of a homework if it exists" in new TestWiring {
    val newStartDate = Timestamp.valueOf("2020-12-1 10:00:00")
    val newDeadlineDate = Timestamp.valueOf("2020-11-21 10:00:00")
    val hwToEdit = testData.homeworks(5)
    val hwEdited = hwToEdit.copy(startDate = newStartDate, deadlineDate = newDeadlineDate)
    whenReady(repo.edit(hwToEdit.homeworkId, hwToEdit.name, hwToEdit.description, newStartDate, newDeadlineDate)) { rowsChanged =>
      rowsChanged shouldBe 1
      whenReady(repo.find(hwToEdit.homeworkId)) {
        _ shouldBe Some(hwEdited)
      }
    }
  }

  "delete" should "drop the homework with a given id" in new TestWiring {
    private val homeworkToDelete = testData.homeworks(3)
    whenReady(repo.delete(homeworkToDelete.homeworkId)) { rowsChanged =>
      rowsChanged shouldBe 1
      whenReady(repo.listAll(homeworkToDelete.courseId)) {
        _.toList shouldBe testData.homeworks.slice(4, 6)
      }
    }
  }

  trait TestWiring {
    val repo: HomeworkRepository = new HomeworkRepositoryImpl
  }
}
