package hw.ppposd.lms

import java.nio.file.{Files, Paths, StandardCopyOption}

import com.typesafe.config.ConfigFactory
import slick.jdbc.JdbcBackend.Database

import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Try}

object TestDatabase {
  lazy val (db, testData) = initializeDatabase()

  private val testConfig = ConfigFactory.defaultApplication()
  private val dbFilePath = s"${testConfig.getString("test.dbFilePath")}.mv.db"
  private val dbBackupFilePath = s"${testConfig.getString("test.dbBackupPath")}.mv.db"

  def initializeDatabase(): (Database, TestData) = {
    Thread.sleep(150) // wait for file to become unoccupied
    deleteDatabase()
    implicit lazy val db: Database = Database.forConfig("test.db")
    Schema.createSchema()
    val testData = SampleDatabaseContent.fillDatabase
    saveDatabase()
    (db, testData)
  }

  def deleteDatabase(): Unit = Files.deleteIfExists(Paths.get(dbFilePath))

  def saveDatabase(): Unit = copyFile(dbFilePath, dbBackupFilePath)

  def restoreDatabase(): Unit = copyFile(dbBackupFilePath, dbFilePath)

  @tailrec
  private def copyFile(src: String, dst: String): Unit = {
    Thread.sleep(150) // wait for db connection to close
    Try {
      Files.copy(Paths.get(src), Paths.get(dst), StandardCopyOption.REPLACE_EXISTING)
      Thread.sleep(150) // wait for copying to finish
    } match {
      case Failure(_) =>
        // file is occupied, wait and retry
        Thread.sleep(100)
        copyFile(src, dst)
      case _ => ()
    }
  }
}
