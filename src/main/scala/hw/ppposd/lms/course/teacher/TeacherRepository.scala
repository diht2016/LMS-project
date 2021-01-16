package hw.ppposd.lms.course.teacher

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import hw.ppposd.lms.user.User
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait TeacherRepository {
  def add(courseId: Id[Course], userId: Id[User]): Future[Int]
  def delete(courseId: Id[Course], userId: Id[User]): Future[Int]
  def listCourseTeacherIds(courseId: Id[Course]): Future[Seq[Id[User]]]
}

class TeacherRepositoryImpl(implicit db: Database) extends TeacherRepository {
  import hw.ppposd.lms.Schema._

  override def add(courseId: Id[Course], userId: Id[User]): Future[Int] = {
    val deleteQuery = courseTeacherLinks.filter(t => t.courseId === courseId && t.teacherId === userId).delete
    val insertQuery = courseTeacherLinks += CourseTeacher(courseId, userId)
    db.run(deleteQuery.andThen(insertQuery))
  }

  override def delete(courseId: Id[Course], userId: Id[User]): Future[Int] =
    db.run(courseTeacherLinks.filter(t => t.courseId === courseId && t.teacherId === userId).delete)

  override def listCourseTeacherIds(courseId: Id[Course]): Future[Seq[Id[User]]] =
    db.run(courseTeacherLinks.filter(_.courseId === courseId).map(_.teacherId).result)
}
