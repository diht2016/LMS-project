package hw.ppposd.lms.base

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

trait MockInOut {
  // use "${NL}" instead of "\n" in control strings
  // because line separator differs on various systems
  lazy val NL: String = outputOf {println()}

  def outputOn(input: String)(f: => Unit): String = {
    val in = new ByteArrayInputStream(input.getBytes)
    val out = new ByteArrayOutputStream()
    Console.withOut(out) {
      Console.withIn(in) (f)
    }
    out.toString
  }

  def outputOf (f: => Unit): String =
    outputOn(input = "") (f)
}
