package hw.ppposd.lms.access

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class CourseTutor(courseId: Id[Course], studentId: Id[User])

class CourseTutorTable(tag: Tag) extends Table[CourseTutor](tag, "course_tutor_links") {
  def courseId = column[Id[Course]]("course_id", O.PrimaryKey)
  def studentId = column[Id[User]]("student_id", O.PrimaryKey)

  def * = (courseId, studentId) <> (CourseTutor.tupled, CourseTutor.unapply)
}
