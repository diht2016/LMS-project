package hw.ppposd.lms.course.homework.solution

import java.sql.Timestamp
import java.time.LocalDateTime

import hw.ppposd.lms.course.homework.Homework
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import hw.ppposd.lms.Schema._
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait SolutionRepository {
  def find(homeworkId: Id[Homework], studentId: Id[User]): Future[Option[Solution]]

  def set(homeworkId: Id[Homework], studentId: Id[User], text: String): Future[Option[Solution]]
}

class SolutionRepositoryImpl(implicit db: Database) extends SolutionRepository {
  override def find(homeworkId: Id[Homework], studentId: Id[User]): Future[Option[Solution]] =
    db.run(solutions.filter(s => s.homeworkId === homeworkId && s.studentId === studentId).result.headOption)

  override def set(homeworkId: Id[Homework], studentId: Id[User], text: String): Future[Option[Solution]] = {
    val deleteQuery = solutions.filter(s => s.homeworkId === homeworkId && s.studentId === studentId).delete
    val insertQuery = solutions += Solution(homeworkId, studentId, text, Timestamp.valueOf(LocalDateTime.now()))
    val findQuery = solutions.filter(s => s.homeworkId === homeworkId && s.studentId === studentId).result.headOption

    db.run(deleteQuery.andThen(insertQuery).andThen(findQuery))
  }



}
