package hw.ppposd.lms.course.homework.solution

import hw.ppposd.lms.base.DatabaseSpecBase

class SolutionRepositorySpec extends DatabaseSpecBase {

  "find" should "return the student's solution for the given homework if it exists" in new TestWiring {

  }

  it should "return None if student hasn't upload given homework yet" in new TestWiring {

  }

  "set" should "save newly student solution for the given homework if it hasn't been solved yet" in new TestWiring {

  }

  it should "update previously uploaded student's solution for the given homework" in new TestWiring {

  }

  trait TestWiring {
    val repo: SolutionRepository = new SolutionRepositoryImpl
  }
}
