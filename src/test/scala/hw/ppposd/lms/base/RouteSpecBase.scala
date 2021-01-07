package hw.ppposd.lms.base

import akka.http.scaladsl.model.HttpMethods.{PATCH, POST, PUT}
import akka.http.scaladsl.model.{ContentTypes, HttpMethod, HttpRequest}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import hw.ppposd.lms.base.JsonSerializer.toJsonString
import play.api.libs.json.Writes

trait RouteSpecBase extends SpecBase
  with ScalatestRouteTest {

  class JsonRequestBuilder(val method: HttpMethod) {
    def apply[T: Writes](uri: String, entity: T): HttpRequest =
      new RequestBuilder(method)(uri)
        .withEntity(ContentTypes.`application/json`, toJsonString(entity))
  }

  val PostJson = new JsonRequestBuilder(POST)
  val PutJson = new JsonRequestBuilder(PUT)
  val PatchJson = new JsonRequestBuilder(PATCH)

  val okResponse = """{"success":true}"""
}
