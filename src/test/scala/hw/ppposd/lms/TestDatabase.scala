package hw.ppposd.lms

import java.nio.file.{Files, Paths, StandardCopyOption}

import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import scala.language.postfixOps

object TestDatabase {
  lazy val db = initializeDatabase()

  // todo: load from test config
  private val dbFilePath = "./target/test.mv.db"
  private val dbBackupFilePath = "./target/test-backup.mv.db"

  private def initializeDatabase(): Database = {
    deleteDatabase()
    implicit lazy val db: Database = Database.forConfig("db")
    Await.ready(Schema.createSchema(db), 3 seconds)
    Await.ready(SampleDatabaseContent.fillDatabase(db), 3 seconds)
    saveDatabase()
    db
  }

  def deleteDatabase(): Unit = Files.deleteIfExists(Paths.get(dbFilePath))

  def saveDatabase(): Unit = copyFile(dbFilePath, dbBackupFilePath)

  def restoreDatabase(): Unit = copyFile(dbBackupFilePath, dbFilePath)

  private def copyFile(src: String, dst: String): Unit = {
    Files.copy(Paths.get(src), Paths.get(dst), StandardCopyOption.REPLACE_EXISTING)
  }
}
