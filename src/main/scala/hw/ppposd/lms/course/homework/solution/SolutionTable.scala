package hw.ppposd.lms.course.homework.solution

import java.sql.Timestamp

import hw.ppposd.lms.course.homework.Homework
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import play.api.libs.json.Json
import slick.jdbc.H2Profile.api._

case class Solution(homeworkId: Id[Homework], studentId: Id[User], text: String, date: Timestamp)

object Solution {
  import hw.ppposd.lms.util.JsonUtils.timestampFormat
  implicit val solutionFormat = Json.format[Solution]
}

class SolutionTable(tag: Tag) extends Table[Solution](tag, "solutions") {
  def homeworkId = column[Id[Homework]]("homework_id")
  def studentId = column[Id[User]]("student_id")
  def test = column[String]("text")
  def date = column[Timestamp]("date")

  def * = (homeworkId, studentId, test, date) .<> ((Solution. apply _).tupled, Solution.unapply)
}
