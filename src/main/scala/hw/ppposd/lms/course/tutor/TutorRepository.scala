package hw.ppposd.lms.course.tutor

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import hw.ppposd.lms.user.User
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait TutorRepository {
  def add(courseId: Id[Course], userId: Id[User]): Future[Int]
  def delete(courseId: Id[Course], userId: Id[User]): Future[Int]
  def listCourseTutorIds(courseId: Id[Course]): Future[Seq[Id[User]]]
}

class TutorRepositoryImpl(implicit db: Database) extends TutorRepository {
  import hw.ppposd.lms.Schema._

  override def add(courseId: Id[Course], userId: Id[User]): Future[Int] =
    db.run(courseTutorLinks += CourseTutor(courseId, userId))

  override def delete(courseId: Id[Course], userId: Id[User]): Future[Int] =
    db.run(courseTutorLinks.filter(t => t.courseId === courseId && t.studentId === userId).delete)

  override def listCourseTutorIds(courseId: Id[Course]): Future[Seq[Id[User]]] =
    db.run(courseTutorLinks.filter(_.courseId === courseId).map(_.studentId).result)
}
