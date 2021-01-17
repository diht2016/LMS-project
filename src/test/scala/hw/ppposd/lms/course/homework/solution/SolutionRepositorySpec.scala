package hw.ppposd.lms.course.homework.solution

import hw.ppposd.lms.TestDatabase.testData
import hw.ppposd.lms.base.DatabaseSpecBase

class SolutionRepositorySpec extends DatabaseSpecBase {

  "find" should "return the student's solution for the given homework if it exists" in new TestWiring {
    private val solutionToFind = testData.solutions(0)
    private val studentId = solutionToFind.studentId
    private val homeworkId = solutionToFind.homeworkId

    whenReady(repo.find(homeworkId, studentId)) {
      _ should be (Some(solutionToFind))
    }
  }

  it should "return None if student hasn't upload given homework yet" in new TestWiring {
    private val studentId = testData.users(2).id
    private val homeworkId = testData.homeworks(0).homeworkId

    whenReady(repo.find(homeworkId, studentId)) {
      _ should be (None)
    }
  }

  "set" should "save newly student solution for the given homework if it hasn't been solved yet" in new TestWiring {
    private val studentId = testData.users(2).id
    private val homeworkId = testData.homeworks(0).homeworkId
    private val text = "text"

    whenReady(repo.set(homeworkId, studentId, text)) {
      _ shouldBe 1
    }
    whenReady(repo.find(homeworkId, studentId)) {
      _.map(_.text) should be (Some(text))
    }
  }

  it should "update previously uploaded student's solution for the given homework" in new TestWiring {
    private val solutionToUpdate = testData.solutions(0)
    private val studentId = solutionToUpdate.studentId
    private val homeworkId = solutionToUpdate.homeworkId
    private val newText = "new text"

    whenReady(repo.set(homeworkId, studentId, newText)) {
      _ shouldBe 1
    }
    whenReady(repo.find(homeworkId, studentId)) {
      _.map(_.text) should be (Some(newText))
    }
  }

  trait TestWiring {
    val repo: SolutionRepository = new SolutionRepositoryImpl
  }
}
