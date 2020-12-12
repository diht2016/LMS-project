package hw.ppposd.lms.solution

import java.sql.Timestamp

import hw.ppposd.lms.homework.Homework
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._


case class Solution(homeworkId: Id[Homework], studentId: Id[User], text: String, date: Timestamp)

class SolutionTable(tag: Tag) extends Table[Solution](tag, "solutions") {
  def homeworkId = column[Id[Homework]]("homework_id", O.PrimaryKey)
  def studentId = column[Id[User]]("student_id", O.PrimaryKey)
  def test = column[String]("text")
  def date = column[Timestamp]("date")

  def * = (homeworkId, studentId, test, date) <> (Solution.tupled, Solution.unapply)
}
