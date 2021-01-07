package hw.ppposd.lms.base

import hw.ppposd.lms.TestDatabase
import org.scalatest.{BeforeAndAfterEach, Canceled, Failed, Outcome, Retries}
import org.scalatest.time.{Millis, Span}
import slick.jdbc.JdbcBackend.Database

trait DatabaseSpecBase extends SpecBase with BeforeAndAfterEach with Retries {

  implicit val db: Database = TestDatabase.db

  implicit val whenReadyConfig: PatienceConfig =
    PatienceConfig(timeout = Span(1000, Millis))

  override def afterEach(): Unit = {
    Thread.sleep(150) // wait for db connection to close
    TestDatabase.restoreDatabase()
    Thread.sleep(150) // wait more
  }

  val retries = 10

  override def withFixture(test: NoArgTest): Outcome = {
    withFixture(test, retries)
  }

  def withFixture(test: NoArgTest, count: Int): Outcome = {
    val outcome = super.withFixture(test)
    //println(test.name, count, outcome)
    outcome match {
      case Failed(_) | Canceled(_) => if (count == 1) {
        println(count)
        super.withFixture(test)
      } else {
        withFixture(test, count - 1)
      }
      case other => other
    }
  }
}
