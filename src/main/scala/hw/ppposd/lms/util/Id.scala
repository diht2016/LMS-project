package hw.ppposd.lms.util

import slick.lifted.MappedTo

class Id[+A](val value: Long) extends AnyVal with MappedTo[Long]

object Id {
  def auto = new Id[Nothing](0)
}