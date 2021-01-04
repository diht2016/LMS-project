package hw.ppposd.lms.base

import hw.ppposd.lms.{SampleDatabaseContent, TestDatabase}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.time.{Millis, Span}
import slick.jdbc.JdbcBackend.Database

trait DatabaseSpecBase extends SpecBase
  with SampleDatabaseContent
  with BeforeAndAfterEach {
  implicit val db: Database = TestDatabase.db
  val oneSecond = timeout(Span(600, Millis))

  override def afterEach(): Unit = {
    Thread.sleep(100)
    TestDatabase.restoreDatabase()
  }
}
