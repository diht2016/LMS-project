package hw.ppposd.lms.course

import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.User

trait CourseRepository {
  def create(name: String, description: String): Future[Id[Course]]
  def find(id: Id[Course]): Future[Option[Course]]
  def list(): Future[Seq[Course]]

  def listTeacherCourseIds(userId: Id[User]): Future[Seq[Id[Course]]]
  def listGroupCourseIds(groupId: Id[Group]): Future[Seq[Id[Course]]]
  def findCourses(courseIds: Seq[Id[Course]]): Future[Seq[Course]]
}

class CourseRepositoryImpl(implicit db: Database) extends CourseRepository {
  import hw.ppposd.lms.Schema._

  override def create(name: String, description: String): Future[Id[Course]] =
    db.run((courses returning courses.map(_.id))
      += Course(Id.auto, name, description))

  override def find(id: Id[Course]): Future[Option[Course]] =
    db.run(courses.filter(_.id === id).result.headOption)

  override def list(): Future[Seq[Course]] =
    db.run(courses.result)

  override def listTeacherCourseIds(userId: Id[User]): Future[Seq[Id[Course]]] =
    db.run(courseTeacherLinks.filter(_.teacherId === userId).map(_.courseId).result)

  override def listGroupCourseIds(groupId: Id[Group]): Future[Seq[Id[Course]]] =
    db.run(groupCourseLinks.filter(_.groupId === groupId).map(_.courseId).result)

  override def findCourses(courseIds: Seq[Id[Course]]): Future[Seq[Course]] =
    db.run(courses.filter(_.id inSet courseIds).result)
}
