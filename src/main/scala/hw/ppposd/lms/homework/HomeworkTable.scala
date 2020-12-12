package hw.ppposd.lms.homework

import java.sql.Timestamp

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class Homework(homeworkId: Id[Homework],
                    courseId: Id[Course],
                    name: String,
                    description: String,
                    startDate: Timestamp,
                    deadlineDate: Timestamp)

class HomeworkTable(tag: Tag) extends Table[Homework](tag, "homworks"){
  def homeworkId = column[Id[Homework]]("homework_id", O.PrimaryKey, O.AutoInc)
  def courseId = column[Id[Course]]("course_id")
  def name = column[String]("name")
  def description = column[String]("description")
  def startDate = column[Timestamp]("start_date")
  def deadlineDate = column[Timestamp]("deadline_date")

  def * = (homeworkId, courseId, name, description, startDate, deadlineDate) <>
    (Homework.tupled, Homework.unapply)
}
