package hw.ppposd.lms.base

import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

trait SpecBase
  extends AnyFlatSpec
  with Matchers
  with MockFactory
  with ScalaFutures
