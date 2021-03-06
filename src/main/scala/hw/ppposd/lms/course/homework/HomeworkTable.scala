package hw.ppposd.lms.course.homework

import java.sql.Timestamp

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._
import play.api.libs.json.{Json, Writes}


case class Homework(homeworkId: Id[Homework],
                    courseId: Id[Course],
                    name: String,
                    description: String,
                    startDate: Timestamp,
                    deadlineDate: Timestamp)

object Homework {
  implicit val homeworkFormat: Writes[Homework] = Json.writes[Homework]
}

class HomeworkTable(tag: Tag) extends Table[Homework](tag, "homeworks"){
  def homeworkId = column[Id[Homework]]("homework_id", O.PrimaryKey, O.AutoInc)
  def courseId = column[Id[Course]]("course_id")
  def name = column[String]("name")
  def description = column[String]("description")
  def startDate = column[Timestamp]("start_date")
  def deadlineDate = column[Timestamp]("deadline_date")


  def * = (homeworkId, courseId, name, description, startDate, deadlineDate)
    .<> ((Homework.apply _).tupled, Homework.unapply)
}
