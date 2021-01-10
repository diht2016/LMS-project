package hw.ppposd.lms.course

import hw.ppposd.lms.course.homework.{HomeworkController, HomeworkRepositoryImpl, HomeworkWiring, HomeworkWiringImpl}
import hw.ppposd.lms.course.material.{MaterialController, MaterialRepositoryImpl}
import hw.ppposd.lms.course.teacher.{TeacherController, TeacherRepositoryImpl}
import hw.ppposd.lms.course.tutor.{TutorController, TutorRepositoryImpl}
import hw.ppposd.lms.group.GroupRepository
import hw.ppposd.lms.user.UserCommons
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContext

trait CourseWiring {
  def teacherController: TeacherController
  def tutorController: TutorController
  def materialController: MaterialController
  def homeworkController: HomeworkController
}

class CourseWiringImpl(accessRepo: AccessRepository, userCommons: UserCommons, groupRepo: GroupRepository)
                      (implicit db: Database, ec: ExecutionContext) extends CourseWiring {
  private val homeworkRepo = new HomeworkRepositoryImpl
  private val materialRepo = new MaterialRepositoryImpl
  private val teacherRepo = new TeacherRepositoryImpl
  private val tutorRepo = new TutorRepositoryImpl

  private val homeworkWiring = new HomeworkWiringImpl(accessRepo, userCommons, groupRepo, homeworkRepo)

  override val homeworkController = new HomeworkController(homeworkRepo, accessRepo, homeworkWiring)
  override val materialController = new MaterialController(materialRepo, accessRepo)
  override val teacherController = new TeacherController(teacherRepo, userCommons)
  override val tutorController = new TutorController(tutorRepo, accessRepo, userCommons)
}
