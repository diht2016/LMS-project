package hw.ppposd.lms.course.homework.solution

import java.sql.Timestamp
import java.time.LocalDateTime

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase

class SolutionRepositorySpec extends DatabaseSpecBase {

  "find" should "return the student's solution for the given homework if it exists" in new TestWiring {
    val solutionToFind = testData.solutions(0)
    val studentId = solutionToFind.studentId
    val homeworkId = solutionToFind.homeworkId

    whenReady(repo.find(homeworkId, studentId)) {
      _ should be (Some(solutionToFind))
    }
  }

  it should "return None if student hasn't upload given homework yet" in new TestWiring {
    val studentId = testData.users(2).id
    val homeworkId = testData.homeworks(0).homeworkId

    whenReady(repo.find(homeworkId, studentId)) {
      _ should be (None)
    }
  }

  "set" should "save newly student solution for the given homework if it hasn't been solved yet" in new TestWiring {
    val studentId = testData.users(2).id
    val homeworkId = testData.homeworks(0).homeworkId
    val text = "text"

    whenReady(repo.set(homeworkId, studentId, text)) {
      _.map(_.text) should be (Some(text))
    }
  }

  it should "update previously uploaded student's solution for the given homework" in new TestWiring {
    val solutionToUpdate = testData.solutions(0)
    val newText = "new text"

    whenReady(repo.set(solutionToUpdate.homeworkId, solutionToUpdate.studentId, newText)) {
      _.map(_.text) should be (Some(newText))
    }
  }

  trait TestWiring {
    val repo: SolutionRepository = new SolutionRepositoryImpl
  }
}
