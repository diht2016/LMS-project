package hw.ppposd.lms.course

import hw.ppposd.lms.course.homework.{HomeworkController, HomeworkRepositoryImpl}
import hw.ppposd.lms.course.material.{MaterialController, MaterialRepositoryImpl}
import hw.ppposd.lms.course.teacher.{TeacherController, TeacherRepositoryImpl}
import hw.ppposd.lms.course.tutor.{TutorController, TutorRepositoryImpl}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContext

trait CourseWiring {
  val teacherController: TeacherController
  val tutorController: TutorController
  val materialController: MaterialController
  val homeworkController: HomeworkController
}

class CourseWiringImpl(accessRepo: AccessRepository)
                      (implicit db: Database, ec: ExecutionContext) extends CourseWiring {
  private val homeworkRepo = new HomeworkRepositoryImpl
  //private val solutionRepo = new SolutionRepositoryImpl
  private val materialRepo = new MaterialRepositoryImpl
  private val teacherRepo = new TeacherRepositoryImpl
  private val tutorRepo = new TutorRepositoryImpl

  val homeworkController = new HomeworkController(homeworkRepo, accessRepo)
  //val solutionController = new SolutionController
  val materialController = new MaterialController(materialRepo, accessRepo)
  override val teacherController = new TeacherController(teacherRepo, accessRepo)
  override val tutorController = new TutorController(tutorRepo, accessRepo)
}
