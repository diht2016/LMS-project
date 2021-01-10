package hw.ppposd.lms.course.homework.solution

import hw.ppposd.lms.base.RouteSpecBase

class SolutionControllerSpec extends RouteSpecBase {

  "SolutionController" should "return 403 if not a teacher requires list of students' solutions" in new TestWiring {

  }

  it should "return grouped list of students' solutions if user is a teacher" in new TestWiring {

  }

  it should "respond with 403 if not a teacher requires solution's text" in new TestWiring {

  }

  it should "return solution's text if user is a teacher" in new TestWiring {

  }

  it should "respond with 404 if a user tries to upload a solution to a non-existent homework" in new TestWiring {

  }

  it should "respond with 403 if a user tries to upload a solution when the deadline has already been passed" in new TestWiring {

  }

  it should "respond with 403 if not a student tries to upload a solution" in new TestWiring {

  }

  it should "respond with 500 if something goes wrong while uploading a solution" in new TestWiring {

  }

  it should "set student's solution for the homework" in new TestWiring {

  }

  trait TestWiring {

  }
}

object SolutionControllerSpec {

}