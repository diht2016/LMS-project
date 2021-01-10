package hw.ppposd.lms.util

import play.api.libs.json.{Format, Json}
import slick.lifted.MappedTo

class Id[+A](val value: Long) extends AnyVal with MappedTo[Long] {
  override def toString: String = value.toString
}

object Id {
  def auto = new Id[Nothing](-1)
  implicit def idJsonFormat[A]: Format[Id[A]] = Json.valueFormat[Id[A]]
}
