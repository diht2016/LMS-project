package hw.ppposd.lms.course.homework

import java.sql.Timestamp
import java.time.LocalDateTime

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase
import hw.ppposd.lms.util.Id

class HomeworkRepositorySpec extends DatabaseSpecBase {

  "find" should "find a homework with a given id" in new TestWiring {
    whenReady(repo.find(testData.homeworks(2).homeworkId)) {
      _ shouldBe Some(testData.homeworks(2))
    }
  }

  "list" should "return all homeworks for the course" in new TestWiring {
    whenReady(repo.list(testData.courses(0).id)) {
      _ shouldBe testData.homeworks.slice(0, 3)
    }
  }

  "listStarted" should "return homeworks with startDate <= current date" in new TestWiring {
    private val now = Timestamp.valueOf("2021-01-05 11:00:00")
    whenReady(repo.listStarted(testData.courses(1).id, now)) {
      _ shouldBe List(testData.homeworks(3), testData.homeworks(4))
    }
  }

  "add" should "create a new homework" in new TestWiring {
    private val hw = Homework(Id.auto,
      testData.courses(1).id,
      "Z.Freud", "Make a report",
      Timestamp.valueOf("2020-11-01 10:00:00"),
      Timestamp.valueOf("2020-11-07 10:00:00"))

    whenReady(repo.add(hw.courseId, hw.name, hw.description, hw.startDate, hw.deadlineDate)) { newId =>
      val newHw = hw.copy(homeworkId = newId)
      whenReady(repo.find(newId)) { res =>
        res shouldBe Some(newHw)
      }
    }
  }

  "edit" should "update fields of a homework if it exists" in new TestWiring {
    private val newStartDate = Timestamp.valueOf("2021-02-01 10:00:00")
    private val newDeadlineDate = Timestamp.valueOf("2021-02-21 10:00:00")
    private val hwToEdit = testData.homeworks(5)
    private val hwEdited = hwToEdit.copy(startDate = newStartDate, deadlineDate = newDeadlineDate)
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
      whenReady(repo.find(homeworkToDelete.homeworkId)) {
        _ shouldBe None
      }
    }
  }

  trait TestWiring {
    val repo: HomeworkRepository = new HomeworkRepositoryImpl
  }
}
