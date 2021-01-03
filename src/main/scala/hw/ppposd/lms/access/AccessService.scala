package hw.ppposd.lms.access

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class AccessService(accessRepo: AccessRepository)(implicit ec: ExecutionContext) {
  def matchUserType[T](userId: Id[User])
                      (ifStudent: Id[Group] => Future[T], ifTeacher: => Future[T]): Future[T] = {
    val groupIdOptFuture = accessRepo.getUserGroupId(userId)
    groupIdOptFuture.flatMap {
      case Some(groupId) => ifStudent(groupId)
      case None => ifTeacher
    }
  }

  def canManageTutors(userId: Id[User], courseId: Id[Course]): Future[Boolean] = {
    accessRepo.isCourseTeacher(userId, courseId)
  }

  def canManageMaterials(userId: Id[User], courseId: Id[Course]): Future[Boolean] = {
    val isTeacher = accessRepo.isCourseTeacher(userId, courseId)
    val isTutor = accessRepo.isCourseTeacher(userId, courseId)
    Future.find(List(isTeacher, isTutor)) { _ == true } map { _.isDefined }
  }

  def listUserCourseBriefs(userId: Id[User]): Future[Seq[CourseBrief]] = {
    matchUserType(userId) (
      ifStudent = groupId => accessRepo.listGroupCourseIds(groupId),
      ifTeacher = accessRepo.listTeacherCourseIds(userId)
    ).flatMap(accessRepo.enrichCourses)
  }

  def listGroupStudentBriefs(groupId: Id[Group]): Future[Seq[UserBrief]] =
    accessRepo.listGroupStudentIds(groupId)
      .flatMap(accessRepo.enrichUsers)

  def listCourseTeacherBriefs(courseId: Id[Course]): Future[Seq[UserBrief]] =
    accessRepo.listCourseTeacherIds(courseId)
      .flatMap(accessRepo.enrichUsers)

  def listCourseTutorBriefs(courseId: Id[Course]): Future[Seq[UserBrief]] =
    accessRepo.listCourseTutorIds(courseId)
      .flatMap(accessRepo.enrichUsers)
}
