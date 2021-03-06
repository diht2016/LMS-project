package hw.ppposd.lms

import akka.http.scaladsl.server._
import hw.ppposd.lms.util.{FutureUtils, Id}
import play.api.libs.json.{Json, Writes}

import scala.concurrent.Future
import scala.language.implicitConversions
import scala.util.{Failure, Success}

trait Controller extends Directives with FutureUtils {
  /**
   * Directive which evaluates its content lazily, only if needed.
   *
   * Example:
   * {{{
   *   concat(
   *     someNonLazyRoute,
   *     otherNonLazyRoute,
   *     delegate(computeHeavyRoute(someArgs))
   *   )
   * }}}
   *
   * Here, function `computeHeavyRoute` won't be called
   * unless both routes above get processed and rejected.
   */
  val delegate: Directive0 = Directive { inner => ctx => inner(())(ctx) }

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
  def assertSingleUpdate: Int => Future[Unit] = {
    case 1 => Future.unit
    case 0 => ApiError(404, "nothing to update")
    case _ => ApiError(500, "failed to update data")
  }

  /**
   * Asserts that the record is found, extracts the value
   * otherwise provides a 404 error
   *
   * Example:
   * {{{
   *   someRepo.findSomething(someQuery)
   *     .flatMap(assertFound("something"))
   *     .flatMap(workWithSomethingHere)
   * }}}
   */
  def assertFound[T](name: String): Option[T] => Future[T] = {
    case Some(t) => Future.successful(t)
    case None => ApiError(404, s"$name not found")
  }
}
