package hw.ppposd.lms

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives
import play.api.libs.json.{Json, Writes}

import scala.concurrent.{ExecutionContext, Future}

trait Controller extends Directives {
  implicit def jsonResponse[T : Writes](entity: Future[T])(implicit ec: ExecutionContext): Route =
    onSuccess(entity.map(Json.toJson(_).toString())) {complete(_)}

  implicit def emptyResponse(entity: Future[Unit])(implicit ec: ExecutionContext): Route =
    onSuccess(entity) {complete("success")}
}
