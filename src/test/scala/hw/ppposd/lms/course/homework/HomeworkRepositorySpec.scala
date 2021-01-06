package hw.ppposd.lms.course.homework

import java.sql.Timestamp

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase
import hw.ppposd.lms.util.Id

class HomeworkRepositorySpec extends DatabaseSpecBase{

  "findA" should "find a homework with a given id" in new TestWiring {
    whenReady(repo.find(testData.homeworks(2).homeworkId), oneSecond) {
      _ should be (Some(testData.homeworks(2)))
    }
  }

  "listAll" should "return all homeworks for the course" in new TestWiring {
    whenReady(repo.listAll(testData.courses(0).id), oneSecond){
      _.toList should be (testData.homeworks.slice(0, 3))
    }
  }

  "listOpened" should "return only homework which have startDate less or equal to the current" in new TestWiring {
    whenReady(repo.listOpened(testData.courses(1).id), oneSecond){
      _.toList should be (List(testData.homeworks(5)))
    }
  }

  "add" should "create a new homework" in new TestWiring {
    val hw = Homework(Id.auto,
      testData.courses(1).id,
      "Z.Freud", "Make a report",
      Timestamp.valueOf("2020-11-1 10:00:00"),
      Timestamp.valueOf("2020-11-7 10:00:00"))

    whenReady(repo.add(hw.courseId, hw.name, hw.description, hw.startDate, hw.deadlineDate), oneSecond) { newId =>
      val newHw = hw.copy(homeworkId = newId)
      whenReady(repo.find(newId), oneSecond) { res =>
        res should be (Some(newHw))
      }
    }
  }

  "edit" should "update fields of a homework if it exists" in new TestWiring {
    val newStartDate = Timestamp.valueOf("2020-12-1 10:00:00")
    val newDeadlineDate = Timestamp.valueOf("2020-11-21 10:00:00")
    val hwToEdit = testData.homeworks(5)
    val hwEdited = hwToEdit.copy(startDate = newStartDate, deadlineDate = newDeadlineDate)
    whenReady(repo.edit(hwToEdit.homeworkId, hwToEdit.name, hwToEdit.description, newStartDate, newDeadlineDate), oneSecond) {
      _ should be (Some(hwEdited))
    }
  }

  "edit" should "return None if homework doesn't exist" in new TestWiring {
    val someDate = Timestamp.valueOf("2020-11-21 10:00:00")
    whenReady(repo.edit(new Id[Homework](11111), "", "", someDate, someDate), oneSecond) {
      _ should be (None)
    }
  }

  "delete" should "drop the homework with a given id" in new TestWiring {
    val idToDelete = testData.homeworks(3).homeworkId
    val idCourse = testData.courses(1).id
    whenReady(repo.delete(idCourse, idToDelete), oneSecond){
      _.toList should be (testData.homeworks.slice(4, 6))
    }
  }

  trait TestWiring {
    val repo: HomeworkRepository = new HomeworkRepositoryImpl
  }
}
