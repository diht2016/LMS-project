package hw.ppposd.lms.course.homework

import java.sql.Timestamp
import java.time.LocalDateTime

import hw.ppposd.lms.Schema._
import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait HomeworkRepository {
  def list(courseId: Id[Course]): Future[Seq[Homework]]
  def listStarted(courseId: Id[Course], now: Timestamp): Future[Seq[Homework]]
  def add(courseId: Id[Course],
          name: String,
          description: String,
          startDate: Timestamp,
          deadlineDate: Timestamp): Future[Id[Homework]]
  def edit(homeworkId: Id[Homework],
           name: String,
           description: String,
           startDate: Timestamp,
           deadlineDate: Timestamp): Future[Int]
  def delete(homeworkId: Id[Homework]): Future[Int]
  def findAndCheckAvailability(id: Id[Homework], now: Timestamp): Future[Option[(Homework, Boolean)]]
}

class HomeworkRepositoryImpl(implicit db: Database) extends HomeworkRepository {

  override def list(courseId: Id[Course]): Future[Seq[Homework]] =
    db.run(homeworks.filter(_.courseId === courseId).result)

  override def listStarted(courseId: Id[Course], now: Timestamp): Future[Seq[Homework]] = {
    db.run(homeworks.filter(hw => hw.courseId === courseId && hw.startDate <= now).result)
  }

  override def add(courseId: Id[Course],
                   name: String,
                   description: String,
                   startDate: Timestamp,
                   deadlineDate: Timestamp): Future[Id[Homework]] =
    db.run((homeworks returning homeworks.map(_.homeworkId))
      += Homework(Id.auto, courseId, name, description, startDate, deadlineDate))


  override def edit(homeworkId: Id[Homework],
                    name: String,
                    description: String,
                    startDate: Timestamp,
                    deadlineDate: Timestamp): Future[Int] =
    db.run(homeworks
      .filter(_.homeworkId === homeworkId)
      .map(h => (h.name, h.description, h.startDate, h.deadlineDate))
      .update(name, description, startDate, deadlineDate))

  override def delete(homeworkId: Id[Homework]): Future[Int] =
    db.run(homeworks.filter(_.homeworkId === homeworkId).delete)

  override def findAndCheckAvailability(id: Id[Homework], now: Timestamp): Future[Option[(Homework, Boolean)]] =
    db.run(homeworks.filter(_.homeworkId === id).map(hw => (hw, hw.deadlineDate >= now && hw.startDate <= now)).result.headOption)
}