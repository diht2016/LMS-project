package hw.ppposd.lms

import akka.http.scaladsl.server.{Directive1, Directives, Route}
import hw.ppposd.lms.util.Id
import play.api.libs.json.{Json, Writes}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait Controller extends Directives {
  /**
   * Directive which extracts [[Id]] of given type `A` from request path.
   *
   * Example:
   * {{{
   *   pathPrefixId[User] { userId =>
   *     // do something with userId here
   *   }
   * }}}
   */
  def pathPrefixId[A]: Directive1[Id[A]] = pathPrefix(LongNumber).map(new Id[A](_))

  /**
   * Provides a HTTP REST API error with code and error message.
   *
   * Example:
   * {{{
   *   userIdOptFuture.flatMap {
   *     case Some(userId) => ??? // do some work and return some successful future here
   *     case None => ApiError(404, "user not found")
   *   }
   * }}}
   */
  case class ApiError(code: Int, message: String) extends Throwable
  object ApiError {
    def apply(code: Int, message: String): Future[Nothing] =
      Future.failed(new ApiError(code, message))
  }

  val successResponse: Route = complete("""{"success":true}""")
  def errorResponse(error: ApiError): Route =
    complete(error.code, s"""{"error":"${error.message}"}""")

  def futureToResponse[T](result: Future[T], completer: T => Route): Route =
    onComplete(result) {
      case Success(writable) => completer(writable)
      case Failure(error: ApiError) => errorResponse(error)
      case Failure(error) =>
        error.printStackTrace()
        errorResponse(new ApiError(500, "internal server error"))
    }

  implicit def futureToResponse(result: Future[Unit]): Route =
    futureToResponse[Unit](result, (_: Unit) => successResponse)

  implicit def futureToResponse[T : Writes](result: Future[T]): Route =
    futureToResponse(result, (writable: T) => complete(Json.toJson(writable).toString()))

  /**
   * Asserts that exactly one row changed after repository method call
   * and replaces this number with a Unit value.
   *
   * Example:
   * {{{
   *   someRepo.updateSomething(something)
   *     .flatMap(assertSingleUpdate)
   * }}}
   */
  def assertSingleUpdate(updated: Int): Future[Unit] = updated match {
    case 1 => Future.unit
    case _ => ApiError(500, "failed to update data")
  }

  def lookupIfSome[A, B](opt: Option[A], f: A => Future[Option[B]]): Future[Option[B]] = opt match {
    case Some(a) => f(a)
    case None => Future.successful(None)
  }

  def lookupIfNeeded[A, B](opt: Option[A], f: => Future[Option[B]]): Future[Option[B]] = opt match {
    case Some(_) => f
    case None => Future.successful(None)
  }
}
