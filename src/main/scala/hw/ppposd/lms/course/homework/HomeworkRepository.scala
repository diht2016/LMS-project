package hw.ppposd.lms.course.homework

import java.sql.Timestamp
import java.time.LocalDateTime

import hw.ppposd.lms.Schema._
import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future


trait HomeworkRepository {

  /**
   * @return Homeworks related to the course with a given courseId
   */
  def listAll(courseId: Id[Course]): Future[Seq[Homework]]

  /**
   * @return Homeworks whose startDate is not after the current date.
   */
  def listOpened(courseId: Id[Course]): Future[Seq[Homework]]

  /**
   * Adds new homework to the course with a given courseId.
   * @return Id of a new homework.
   */
  def add(courseId: Id[Course],
          name: String,
          description: String,
          startDate: Timestamp,
          deadlineDate: Timestamp): Future[Id[Homework]]

  /**
   * Updates fields of a homework with a given homeworkId if it exists
   * @return Updated homework if it exists, None else.
   */
  def edit(homeworkId: Id[Homework],
           name: String,
           description: String,
           startDate: Timestamp,
           deadlineDate: Timestamp): Future[Option[Homework]]

  /**
   * Deletes homework with a given homeworkId.
   * @return List of remained homeworks for the course with a given courseId.
   */
  def delete(courseId: Id[Course], homeworkId: Id[Homework]): Future[Seq[Homework]]

  /**
   * @return Homework with a given id if it exists, None otherwise.
   */
  def find(id: Id[Homework]): Future[Option[Homework]]
}

class HomeworkRepositoryImpl(implicit db: Database) extends HomeworkRepository {

  override def listAll(courseId: Id[Course]): Future[Seq[Homework]] =
    db.run(homeworks.filter(_.courseId === courseId).result)

  override def listOpened(courseId: Id[Course]): Future[Seq[Homework]] = {
    val now = Timestamp.valueOf(LocalDateTime.now)
    db.run(homeworks.filter(hw => hw.courseId === courseId && hw.startDate <= now).result)
  }

  override def add(courseId: Id[Course],
                   name: String,
                   description: String,
                   startDate: Timestamp,
                   deadlineDate: Timestamp): Future[Id[Homework]] =
    db.run((homeworks returning homeworks.map(_.homeworkId))
      += Homework(Id.auto, courseId, name, description, startDate, deadlineDate)
    )


  override def edit(homeworkId: Id[Homework],
                    name: String,
                    description: String,
                    startDate: Timestamp,
                    deadlineDate: Timestamp): Future[Option[Homework]] = {
    val updateQuery = homeworks
      .filter(_.homeworkId === homeworkId)
      .map(h => (h.name, h.description, h.startDate, h.deadlineDate))
      .update(name, description, startDate, deadlineDate)
    db.run(updateQuery.andThen(homeworks.filter(_.homeworkId === homeworkId).result.headOption))
  }

  override def delete(courseId: Id[Course], homeworkId: Id[Homework]): Future[Seq[Homework]] = {
    val deleteQuery = homeworks
      .filter(_.homeworkId === homeworkId)
      .delete
    db.run(deleteQuery.andThen(homeworks.filter(_.courseId === courseId).result))
  }

  override def find(id: Id[Homework]): Future[Option[Homework]] =
    db.run(homeworks.filter(_.homeworkId === id).result.headOption)
}