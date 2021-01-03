package hw.ppposd.lms

import hw.ppposd.lms.access.{AccessRepositoryImpl, AccessService}
import hw.ppposd.lms.auth.{AuthController, AuthRepositoryImpl}
import hw.ppposd.lms.course.{CourseController, CourseRepositoryImpl}
import hw.ppposd.lms.group.{GroupController, GroupRepositoryImpl}
import hw.ppposd.lms.user.{UserController, UserRepositoryImpl}
import hw.ppposd.lms.Schema._

import scala.concurrent.ExecutionContext

class Wiring(implicit ec: ExecutionContext) {
  val accessRepo = new AccessRepositoryImpl
  val authRepo = new AuthRepositoryImpl
  val courseRepo = new CourseRepositoryImpl
  val groupRepo = new GroupRepositoryImpl
  val userRepo = new UserRepositoryImpl

  val accessService = new AccessService(accessRepo)

  object Controllers {
    val authController = new AuthController(authRepo)
    val courseController = new CourseController(courseRepo, accessService)
    val groupController = new GroupController(accessService)
    val userController = new UserController(userRepo, groupRepo)
  }
}
