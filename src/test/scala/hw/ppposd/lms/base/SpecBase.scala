package hw.ppposd.lms.base

import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

trait SpecBase
  extends AnyFlatSpec
  with Matchers
  with MockFactory
