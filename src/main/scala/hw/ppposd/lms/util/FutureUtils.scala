package hw.ppposd.lms.util

import scala.concurrent.{ExecutionContext, Future}

object FutureUtils {
  def failOnFalseWith(error: => Future[Unit])(condition: Boolean): Future[Unit] =
    if (condition) Future.unit else error

  def anyTrue(futures: Future[Boolean]*)(implicit ec: ExecutionContext): Future[Boolean] =
    Future.find(futures) { _ == true } map { _.isDefined }

  def allTrue(futures: Future[Boolean]*)(implicit ec: ExecutionContext): Future[Boolean] =
    Future.find(futures) { !_ } map { _.isEmpty }
}
