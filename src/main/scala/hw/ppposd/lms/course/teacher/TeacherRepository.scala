package hw.ppposd.lms.course.teacher

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import hw.ppposd.lms.user.User
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait TeacherRepository {
  def add(courseId: Id[Course], userId: Id[User]): Future[Int]
  def delete(courseId: Id[Course], userId: Id[User]): Future[Int]
  def listCourseTutorIds(courseId: Id[Course]): Future[Seq[Id[User]]]
}

class TeacherRepositoryImpl(implicit db: Database) extends TeacherRepository {
  import hw.ppposd.lms.Schema._

  override def add(courseId: Id[Course], userId: Id[User]): Future[Int] =
    db.run(courseTeacherLinks += CourseTeacher(courseId, userId))

  override def delete(courseId: Id[Course], userId: Id[User]): Future[Int] =
    db.run(courseTeacherLinks.filter(t => t.courseId === courseId && t.teacherId === userId).delete)

  override def listCourseTutorIds(courseId: Id[Course]): Future[Seq[Id[User]]] =
    db.run(courseTeacherLinks.filter(_.courseId === courseId).map(_.teacherId).result)
}
