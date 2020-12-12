package hw.ppposd.lms.links

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class CourseTeacher(courseId: Id[Course], teacherId: Id[User])

class CourseTeacherTable(tag: Tag) extends Table[CourseTeacher](tag, "course_teacher_links"){
  def courseId = column[Id[Course]]("course_id", O.PrimaryKey)
  def teacherId = column[Id[User]]("teacher_id", O.PrimaryKey)

  def * = (courseId, teacherId) <> (CourseTeacher.tupled, CourseTeacher.unapply)
}
