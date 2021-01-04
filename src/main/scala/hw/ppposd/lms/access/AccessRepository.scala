package hw.ppposd.lms.access

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ExecutionContext, Future}

trait AccessRepository {
  def getUserGroupId(userId: Id[User]): Future[Option[Id[Group]]]
  def listGroupStudentIds(groupId: Id[Group]): Future[Seq[Id[User]]]
  def listGroupCourseIds(groupId: Id[Group]): Future[Seq[Id[Course]]]
  def listTeacherCourseIds(userId: Id[User]): Future[Seq[Id[Course]]]
  def listCourseTeacherIds(courseId: Id[Course]): Future[Seq[Id[User]]]
  def listCourseTutorIds(courseId: Id[Course]): Future[Seq[Id[User]]]
  def isCourseTeacher(userId: Id[User], courseId: Id[Course]): Future[Boolean]
  def isCourseTutor(userId: Id[User], courseId: Id[Course]): Future[Boolean]

  def enrichUsers(userIds: Seq[Id[User]]): Future[Seq[UserBrief]]
  def enrichCourses(courseIds: Seq[Id[Course]]): Future[Seq[CourseBrief]]
}

class AccessRepositoryImpl(implicit db: Database, ec: ExecutionContext) extends AccessRepository {
  import hw.ppposd.lms.Schema._

  override def getUserGroupId(userId: Id[User]): Future[Option[Id[Group]]] =
    db.run(users.filter(_.id === userId).map(_.groupId).result.head)

  override def listGroupStudentIds(groupId: Id[Group]): Future[Seq[Id[User]]] =
    db.run(users.filter(_.groupId === groupId).map(_.id).result)

  override def listGroupCourseIds(groupId: Id[Group]): Future[Seq[Id[Course]]] =
    db.run(groupCourseLinks.filter(_.groupId === groupId).map(_.courseId).result)

  override def listTeacherCourseIds(userId: Id[User]): Future[Seq[Id[Course]]] =
    db.run(courseTeacherLinks.filter(_.teacherId === userId).map(_.courseId).result)

  override def listCourseTeacherIds(courseId: Id[Course]): Future[Seq[Id[User]]] =
    db.run(courseTeacherLinks.filter(_.courseId === courseId).map(_.teacherId).result)

  override def listCourseTutorIds(courseId: Id[Course]): Future[Seq[Id[User]]] =
    db.run(courseTutorLinks.filter(_.courseId === courseId).map(_.studentId).result)

  override def isCourseTeacher(userId: Id[User], courseId: Id[Course]): Future[Boolean] =
    db.run(courseTeacherLinks.filter(x => x.teacherId === userId && x.courseId === courseId)
      .map(_ => ()).result.headOption).map(_.isDefined)

  override def isCourseTutor(userId: Id[User], courseId: Id[Course]): Future[Boolean] =
    db.run(courseTutorLinks.filter(x => x.studentId === userId && x.courseId === courseId)
      .map(_ => ()).result.headOption).map(_.isDefined)

  override def enrichUsers(userIds: Seq[Id[User]]): Future[Seq[UserBrief]] =
    db.run(users.filter(_.id inSet userIds).map(x => (x.id, x.fullName)).result
      .map(seq => seq.map((UserBrief.apply _).tupled)))

  override def enrichCourses(courseIds: Seq[Id[Course]]): Future[Seq[CourseBrief]] =
    db.run(courses.filter(_.id inSet courseIds).map(x => (x.id, x.name)).result
      .map(seq => seq.map((CourseBrief.apply _).tupled)))
}
