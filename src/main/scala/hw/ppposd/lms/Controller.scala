package hw.ppposd.lms

import akka.http.scaladsl.server.{Directives, Route}
import play.api.libs.json.{Json, Writes}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait Controller extends Directives {
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

  def assertSingleUpdate(updated: Int): Future[Unit] = updated match {
    case 1 => Future.unit
    case _ => ApiError(500, "failed to update data")
  }
}
