package hw.ppposd.lms.course

import hw.ppposd.lms.group.Group
import hw.ppposd.lms.user.{User, UserBrief}
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ExecutionContext, Future}

trait AccessRepository {
  def findUserGroupId(userId: Id[User]): Future[Option[Id[Group]]]
  def isCourseTeacher(userId: Id[User], courseId: Id[Course]): Future[Boolean]
  def isCourseTutor(userId: Id[User], courseId: Id[Course]): Future[Boolean]
  def isCourseStudent(userId: Id[User], courseId: Id[Course]): Future[Boolean]
  def enrichUsers(userIds: Seq[Id[User]]): Future[Seq[UserBrief]]
}

class AccessRepositoryImpl(implicit db: Database, ec: ExecutionContext) extends AccessRepository {
  import hw.ppposd.lms.Schema._

  override def findUserGroupId(userId: Id[User]): Future[Option[Id[Group]]] =
    db.run(users.filter(_.id === userId).map(_.groupId).result.head)

  override def isCourseTeacher(userId: Id[User], courseId: Id[Course]): Future[Boolean] =
    db.run(courseTeacherLinks.filter(x => x.teacherId === userId && x.courseId === courseId)
      .result.headOption).map(_.isDefined)

  override def isCourseTutor(userId: Id[User], courseId: Id[Course]): Future[Boolean] =
    db.run(courseTutorLinks.filter(x => x.studentId === userId && x.courseId === courseId)
      .result.headOption).map(_.isDefined)

  override def isCourseStudent(userId: Id[User], courseId: Id[Course]): Future[Boolean] =
    db.run(users.filter(_.id === userId)
      .join(groupCourseLinks.filter(_.courseId === courseId))
      .on(_.groupId === _.groupId).result.headOption).map(_.isDefined)

  override def enrichUsers(userIds: Seq[Id[User]]): Future[Seq[UserBrief]] =
    db.run(users.filter(_.id inSet userIds).map(x => (x.id, x.fullName)).result
      .map(seq => seq.map((UserBrief.apply _).tupled)))
}
