package hw.ppposd.lms

import hw.ppposd.lms.auth.{AuthController, AuthRepositoryImpl}
import hw.ppposd.lms.course.{AccessRepositoryImpl, CourseController, CourseRepositoryImpl, CourseWiringImpl}
import hw.ppposd.lms.group.{GroupController, GroupRepositoryImpl}
import hw.ppposd.lms.user.{UserCommonsImpl, UserController, UserRepositoryImpl}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContext

trait RootWiring {
  val authController: AuthController
  val courseController: CourseController
  val groupController: GroupController
  val userController: UserController
}

class RootWiringImpl(implicit db: Database, ec: ExecutionContext) extends RootWiring {
  private val authRepo = new AuthRepositoryImpl
  private val courseRepo = new CourseRepositoryImpl
  private val groupRepo = new GroupRepositoryImpl
  private val userRepo = new UserRepositoryImpl

  private val accessRepo = new AccessRepositoryImpl
  private val userCommons = new UserCommonsImpl
  private val courseWiring = new CourseWiringImpl(accessRepo, userCommons)

  override val authController = new AuthController(authRepo)
  override val courseController = new CourseController(courseRepo, userCommons, courseWiring)
  override val groupController = new GroupController(groupRepo, userCommons)
  override val userController = new UserController(userRepo, groupRepo)
}
