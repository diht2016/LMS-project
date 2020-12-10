package hw.ppposd.lms

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

trait SpecBase
  extends AnyFlatSpec
  with Matchers
  with MockFactory
  with ScalatestRouteTest