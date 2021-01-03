package hw.ppposd.lms

import akka.actor.ActorSystem
import java.io._
import java.net.URL
import sys.process._

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.io.Source

trait SpecBaseDb extends AnyFlatSpec
  with Matchers
  with BeforeAndAfterEach
  with BeforeAndAfterAll {

  implicit val system: ActorSystem = ActorSystem("lms-system")
  implicit val ec: ExecutionContext = system.dispatcher

  private val timeout = 5.seconds
  private val testDbPath = "jdbc:h2:./target/testdb"
  private val backupDbPath = "jdbc:h2:./target/backupdb"

  override def afterEach(): Unit = restoreDb()

  override def beforeAll(): Unit = {
    val prepareDb = for {
      _ <- SpecSchema.dropDb()
      _ <- SpecSchema.setupDb()
      _ <- backupDb()
    } yield ()

    Await.result(prepareDb, timeout)
  }

  private def restoreDb(): Unit = {
    val src = backupDbPath
    val dst = testDbPath
    val inputChannel = new FileInputStream(src).getChannel
    val outputChannel = new FileOutputStream(dst).getChannel;
    outputChannel.transferFrom(inputChannel, 0, inputChannel.size())
    inputChannel.close()
    outputChannel.close()
  }

  private def backupDb(): Future[Unit] = Future.successful({
    val src = testDbPath
    val dst = backupDbPath

    val inputChannel = new FileInputStream(src).getChannel
    val outputChannel = new FileOutputStream(dst).getChannel
    outputChannel.transferFrom(inputChannel, 0, inputChannel.size())
    inputChannel.close()
    outputChannel.close()
  })
}
