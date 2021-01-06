package hw.ppposd.lms.course.material

import hw.ppposd.lms.Controller
import hw.ppposd.lms.course.{AccessRepository, Course}
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class MaterialController(accessRepo: AccessRepository)
                        (implicit ec: ExecutionContext) extends Controller {
  // TODO

  private def canManageMaterials(userId: Id[User], courseId: Id[Course]): Future[Boolean] = {
    val isTeacher = accessRepo.isCourseTeacher(userId, courseId)
    val isTutor = accessRepo.isCourseTeacher(userId, courseId)
    Future.find(List(isTeacher, isTutor)) { _ == true } map { _.isDefined }
  }
}
