package hw.ppposd.lms.base

import hw.ppposd.lms.TestDatabase
import org.scalatest.BeforeAndAfterEach
import org.scalatest.time.{Millis, Span}
import slick.jdbc.JdbcBackend.Database

trait DatabaseSpecBase extends SpecBase with BeforeAndAfterEach {

  implicit val db: Database = TestDatabase.db

  implicit val whenReadyConfig: PatienceConfig =
    PatienceConfig(timeout = Span(900, Millis))

  override def afterEach(): Unit = {
    Thread.sleep(100) // wait for db connection to close
    TestDatabase.restoreDatabase()
  }
}
