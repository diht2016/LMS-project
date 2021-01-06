package hw.ppposd.lms.course.homework

import hw.ppposd.lms.base.RouteSpecBase
import hw.ppposd.lms.course.{AccessRepository, CourseWiring}

class HomeworkControllerSpec extends RouteSpecBase {

  "HomeworkController" should "return list of all course homeworks if user is teacher" in new TestWiring {

  }

  it should "return list of opened course homeworks if user is student" in new TestWiring {

  }

  it should "create new homework if user is a teacher" in new TestWiring {

  }

  it should "throw error 403 if not teacher is trying to create new homework" in new TestWiring {

  }

  it should "delete homework if user is a teacher" in new TestWiring {

  }

  it should "throw error 403 if not teacher is trying to delete course homework" in new TestWiring {

  }

  it should "edit homework if user is a teacher" in new TestWiring {

  }

  it should "throw error 403 if not teacher is trying to edit homework" in new TestWiring {

  }

  trait TestWiring {
    val accessRepoMock: AccessRepository = mock[AccessRepository]
    val homeworkRepoMock: HomeworkRepository = mock[HomeworkRepository]
    val wiringMock: CourseWiring = mock[CourseWiring]
    val controller = new HomeworkController(homeworkRepoMock, accessRepoMock)
  }
}
