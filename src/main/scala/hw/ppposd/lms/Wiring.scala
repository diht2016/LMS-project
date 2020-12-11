package hw.ppposd.lms

import hw.ppposd.lms.auth.{AuthController, AuthRepositoryImpl}
import hw.ppposd.lms.course.{CourseController, CourseRepositoryImpl}

object Wiring {
  object Repositories {
    val authRepo = new AuthRepositoryImpl
    val courseRepo = new CourseRepositoryImpl
  }

  object Controllers {
    val authController = new AuthController(Repositories.authRepo)
    val courseController = new CourseController(Repositories.courseRepo)
  }
}
