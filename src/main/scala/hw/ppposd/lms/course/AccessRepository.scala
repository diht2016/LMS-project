package hw.ppposd.lms.course

import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait AccessRepository {
  def isCourseTeacher(userId: Id[User], courseId: Id[Course]): Future[Boolean]
  def isCourseTutor(userId: Id[User], courseId: Id[Course]): Future[Boolean]
  def isCourseStudent(userId: Id[User], courseId: Id[Course]): Future[Boolean]
}

class AccessRepositoryImpl(implicit db: Database) extends AccessRepository {
  import hw.ppposd.lms.Schema._

  override def isCourseTeacher(userId: Id[User], courseId: Id[Course]): Future[Boolean] =
    db.run(courseTeacherLinks.filter(x => x.teacherId === userId && x.courseId === courseId)
      .exists.result)

  override def isCourseTutor(userId: Id[User], courseId: Id[Course]): Future[Boolean] =
    db.run(courseTutorLinks.filter(x => x.studentId === userId && x.courseId === courseId)
      .exists.result)

  override def isCourseStudent(userId: Id[User], courseId: Id[Course]): Future[Boolean] =
    db.run(users.filter(_.id === userId)
      .join(groupCourseLinks.filter(_.courseId === courseId))
      .on(_.groupId === _.groupId).exists.result)
}
