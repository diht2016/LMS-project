package hw.ppposd.lms.links

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.group.Group
import hw.ppposd.lms.util.Id
import slick.jdbc.H2Profile.api._

case class GroupCourse(groupId: Id[Group], courseId: Id[Course])

class GroupCourseTable(tag: Tag) extends Table[GroupCourse](tag, "group_course_links"){
  def groupId = column[Id[Group]]("group_id", O.PrimaryKey)
  def courseId = column[Id[Course]]("course_id", O.PrimaryKey)

  def * = (groupId, courseId) <> ((GroupCourse.apply _).tupled, GroupCourse.unapply)
}
