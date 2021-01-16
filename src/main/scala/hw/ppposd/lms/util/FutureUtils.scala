package hw.ppposd.lms.util

import scala.concurrent.{ExecutionContext, Future}

trait FutureUtils {
  def anyTrue(futures: Future[Boolean]*)(implicit ec: ExecutionContext): Future[Boolean] =
    Future.find(futures) { _ == true } map { _.isDefined }

  def allTrue(futures: Future[Boolean]*)(implicit ec: ExecutionContext): Future[Boolean] =
    Future.find(futures) { !_ } map { _.isEmpty }

  def assertTrue(conditionFuture: Future[Boolean], error: => Future[Unit])
                (implicit ec: ExecutionContext): Future[Unit] =
    conditionFuture.flatMap { if (_) Future.unit else error }

  implicit class ChainFuture[+A](future: Future[A]) {
    def accept[B](other: => Future[B])(implicit ec: ExecutionContext): Future[B] =
      future.flatMap(_ => other)
  }

  def lookupIfSome[A, B](opt: Option[A], f: A => Future[Option[B]]): Future[Option[B]] =
    opt match {
      case Some(a) => f(a)
      case None => Future.successful(None)
    }

  def lookupIfNeeded[A, B](opt: Option[A], f: => Future[Option[B]]): Future[Option[B]] =
    opt match {
      case Some(_) => f
      case None => Future.successful(None)
    }
}
