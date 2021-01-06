package hw.ppposd.lms.course.homework

import java.security.Timestamp

import hw.ppposd.lms.access.AccessService
import hw.ppposd.lms.course.Course
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class HomeworkService(homeworkRepo: HomeworkRepository, accessService: AccessService)(implicit ec: ExecutionContext) {
  def listHomeworksOfCourse(userId: Id[User], courseId: Id[Course]): Future[Seq[Homework]] = {
    accessService.matchUserType(userId) (
      ifStudent = _ => homeworkRepo.listOpened(courseId),
      ifTeacher = homeworkRepo.listAll(courseId)
    )
  }

  def createNewHomework(userId: Id[User],
                        courseId: Id[Course],
                        name: String,
                        description: String,
                        startDate: Timestamp,
                        deadlineDate: Timestamp): Future[Id[Homework]] = {
    accessService.matchUserType(userId) (
      ifStudent = _ => Future.successful(new Exception())
    )
  }

}
