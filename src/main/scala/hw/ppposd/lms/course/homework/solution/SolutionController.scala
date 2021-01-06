package hw.ppposd.lms.course.homework.solution

import java.time.LocalDateTime

import akka.http.scaladsl.server.Route
import hw.ppposd.lms.Controller
import hw.ppposd.lms.course.homework.{Homework, HomeworkRepository}
import hw.ppposd.lms.course.{AccessRepository, Course}
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id

import scala.concurrent.{ExecutionContext, Future}

class SolutionController(accessRepo: AccessRepository, solutionRepo: SolutionRepository, homeworkRepo: HomeworkRepository)
                        (implicit ec: ExecutionContext) extends Controller {
/*
  def route(userId: Id[User], courseId: Id[Course], homeworkId: Id[Homework]): Route = {

  }
*/
  def getSolutionText(userId: Id[User], courseId: Id[Course], studentId: Id[User], homeworkId: Id[Homework]): Future[Option[String]] =
    for {
      isCourseTeacher <- accessRepo.isCourseTeacher(userId, courseId)
      maybeSolution <-
        if (isCourseTeacher) {
          solutionRepo.find(homeworkId, studentId)
        } else {
          ApiError(403, "User is not a teacher of a course")
        }
    } yield maybeSolution.map(_.text)

  def uploadSolution(userId: Id[User], courseId: Id[Course], homeworkId: Id[Homework], text: String): Future[Solution] =
    for {
      isCourseStudent <- accessRepo.isCourseStudent(userId, courseId)
      maybeHomework <- homeworkRepo.find(homeworkId)
      _ <- if (maybeHomework.isEmpty) ApiError(404, s"Homework with id=$homeworkId does not exits.") else Future.unit
      isHomeworkOpened = maybeHomework.exists(hw => hw.deadlineDate.toLocalDateTime.isAfter(LocalDateTime.now()))
      _ <- if (!isHomeworkOpened) ApiError(403, "Homework deadline has been already passed.") else Future.unit
      solution <-
        if (isCourseStudent ) {
          solutionRepo.set(homeworkId, userId, text).flatMap{
            case Some(s) => Future.successful(s)
            case None => ApiError(500, "Failed to upload solution")
          }
        } else {
          ApiError(403, "User is not a course student")
        }
    } yield solution

  def getStudentsSolutionsForHomework(userId: Id[User], courseId: Id[Course], homeworkId: Id[Homework]) = {

  }

}
