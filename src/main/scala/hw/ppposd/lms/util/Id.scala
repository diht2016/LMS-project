package hw.ppposd.lms.util

import play.api.libs.json.{JsPath, Json, Writes}
import slick.ast.TypedType
import slick.lifted.MappedTo

class Id[+A](val value: Long) extends AnyVal with MappedTo[Long]

object Id {
  def auto = new Id[Nothing](0)
  implicit val idJsonFormat = ???
}

