package hw.ppposd.lms.util

import java.sql.Timestamp
import java.time.LocalDateTime

import play.api.libs.json.{Format, JsResult, JsValue}
import play.api.libs.json.Json.{fromJson, toJson}

object JsonUtils {
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    def writes(t: Timestamp): JsValue = toJson(t.toLocalDateTime)
    def reads(json: JsValue): JsResult[Timestamp] = fromJson[LocalDateTime](json).map(Timestamp.valueOf)
  }
}
