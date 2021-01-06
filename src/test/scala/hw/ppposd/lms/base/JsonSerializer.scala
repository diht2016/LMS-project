package hw.ppposd.lms.base

import play.api.libs.json.{Json, Writes}

object JsonSerializer {
  def toJsonString[T : Writes](writable: T): String = Json.toJson(writable).toString()
}
