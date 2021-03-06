package hw.ppposd.lms.group

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class GroupCourse(groupId: Id[Group], courseId: Id[Course])

class GroupCourseTable(tag: Tag) extends Table[GroupCourse](tag, "group_course_links"){
  def groupId = column[Id[Group]]("group_id")
  def courseId = column[Id[Course]]("course_id")

  def * = (groupId, courseId) .<> ((GroupCourse.apply _).tupled, GroupCourse.unapply)
}
