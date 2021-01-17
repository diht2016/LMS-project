package hw.ppposd.lms.course.homework

import java.sql.Timestamp
import java.time.LocalDateTime

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase
import hw.ppposd.lms.util.Id

class HomeworkRepositorySpec extends DatabaseSpecBase {

  "find" should "find a homework with a given id" in new TestWiring {
    val now: Timestamp = Timestamp.valueOf("2020-12-14 23:59:59")

    whenReady(repo.findAndCheckAvailability(testData.homeworks(2).homeworkId, now)) {
      _ shouldBe Some((testData.homeworks(2), false))
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
    val now: Timestamp = Timestamp.valueOf("2020-12-14 23:59:59")

    private val hw = Homework(Id.auto,
      testData.courses(1).id,
      "Z.Freud", "Make a report",
      Timestamp.valueOf("2020-11-01 10:00:00"),
      Timestamp.valueOf("2020-11-07 10:00:00"))

    whenReady(repo.add(hw.courseId, hw.name, hw.description, hw.startDate, hw.deadlineDate)) { newId =>
      val newHw = hw.copy(homeworkId = newId)
      whenReady(repo.findAndCheckAvailability(newId, now)) { res =>
        res shouldBe Some(newHw, false)
      }
    }
  }

  "edit" should "update fields of a homework if it exists" in new TestWiring {
    val now: Timestamp = Timestamp.valueOf("2021-02-09 10:00:00")

    private val newStartDate = Timestamp.valueOf("2021-02-01 10:00:00")
    private val newDeadlineDate = Timestamp.valueOf("2021-02-21 10:00:00")
    private val hwToEdit = testData.homeworks(5)
    private val hwEdited = hwToEdit.copy(startDate = newStartDate, deadlineDate = newDeadlineDate)
    whenReady(repo.edit(hwToEdit.homeworkId, hwToEdit.name, hwToEdit.description, newStartDate, newDeadlineDate)) { rowsChanged =>
      rowsChanged shouldBe 1
      whenReady(repo.findAndCheckAvailability(hwToEdit.homeworkId, now)) {
        _ shouldBe Some(hwEdited, true)
      }
    }
  }

  "delete" should "drop the homework with a given id" in new TestWiring {
    val now: Timestamp = Timestamp.valueOf("2020-12-14 23:59:59")

    private val homeworkToDelete = testData.homeworks(3)
    whenReady(repo.delete(homeworkToDelete.homeworkId)) { rowsChanged =>
      rowsChanged shouldBe 1
      whenReady(repo.findAndCheckAvailability(homeworkToDelete.homeworkId, now)) {
        _ shouldBe None
      }
    }
  }

  trait TestWiring {
    val repo: HomeworkRepository = new HomeworkRepositoryImpl
  }
}
