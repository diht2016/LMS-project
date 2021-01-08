package hw.ppposd.lms

import hw.ppposd.lms.base.{MockInOut, SpecBase}

class ApplicationSpec extends SpecBase with MockInOut {

  "Application" should "start and shutdown on newline in input" in {
    val app = Application
    val port = ApplicationConfig.port
    val expectedOutput =
      s"""Server running at http://localhost:$port/
         |Press RETURN to stop
         |""".stripMargin

    outputOn(NL) {
      app.main(Array())
    } shouldBe expectedOutput
  }
}
