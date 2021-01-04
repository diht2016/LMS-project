package hw.ppposd.lms

import java.nio.file.{Files, Paths, StandardCopyOption}

import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import scala.language.postfixOps

object TestDatabase {
  lazy val (db, testData) = initializeDatabase()

  // todo: load from test config
  private val dbFilePath = "./target/test.mv.db"
  private val dbBackupFilePath = "./target/test-backup.mv.db"

  private def initializeDatabase(): (Database, TestData) = {
    deleteDatabase()
    implicit lazy val db: Database = Database.forConfig("db")
    Await.ready(Schema.createSchema, 3 seconds)
    val testData = SampleDatabaseContent.fillDatabase
    saveDatabase()
    (db, testData)
  }

  def deleteDatabase(): Unit = Files.deleteIfExists(Paths.get(dbFilePath))

  def saveDatabase(): Unit = copyFile(dbFilePath, dbBackupFilePath)

  def restoreDatabase(): Unit = copyFile(dbBackupFilePath, dbFilePath)

  private def copyFile(src: String, dst: String): Unit = {
    Files.copy(Paths.get(src), Paths.get(dst), StandardCopyOption.REPLACE_EXISTING)
  }
}
