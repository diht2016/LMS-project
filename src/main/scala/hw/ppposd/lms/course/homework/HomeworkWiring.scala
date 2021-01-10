package hw.ppposd.lms.course.homework

import hw.ppposd.lms.course.AccessRepository
import hw.ppposd.lms.course.homework.solution.{SolutionController, SolutionRepository, SolutionRepositoryImpl}
import hw.ppposd.lms.group.{GroupRepository, GroupRepositoryImpl}
import hw.ppposd.lms.user.UserCommons
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.ExecutionContext

trait HomeworkWiring {
  def solutionController: SolutionController
}

class HomeworkWiringImpl(accessRepo: AccessRepository,
                         userCommons: UserCommons,
                         groupRepo: GroupRepository,
                         homeworkRepo: HomeworkRepository)
                      (implicit db: Database, ec: ExecutionContext) extends HomeworkWiring {
  private val solutionRepo = new SolutionRepositoryImpl

  override val solutionController = new SolutionController(accessRepo, solutionRepo, homeworkRepo, groupRepo, userCommons)
 }
