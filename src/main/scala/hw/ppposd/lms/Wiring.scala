package hw.ppposd.lms

import hw.ppposd.lms.course.{CourseController, CourseRepositoryImpl}

object Wiring {
  object Repositories {
    val courseRepo = new CourseRepositoryImpl
  }

  object Controllers {
    val courseController = new CourseController(Repositories.courseRepo)
  }
}
