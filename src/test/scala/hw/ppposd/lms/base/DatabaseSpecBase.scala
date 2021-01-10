package hw.ppposd.lms.base

import hw.ppposd.lms.TestDatabase
import org.scalatest.BeforeAndAfterEach
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt

trait DatabaseSpecBase extends SpecBase with BeforeAndAfterEach {

  implicit val db: Database = TestDatabase.db

  def whenReady[T](future: Future[T])(action: T => Unit): Unit =
    action(Await.result(future, 5.seconds))

  override def afterEach(): Unit =
    TestDatabase.restoreDatabase()
}
