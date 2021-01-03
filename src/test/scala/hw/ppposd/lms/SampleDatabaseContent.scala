package hw.ppposd.lms

import hw.ppposd.lms.course.Course
import hw.ppposd.lms.user.User
import hw.ppposd.lms.util.Id
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait SampleDatabaseContent {
  /**
   * Public test data, can be used in database tests
   */

  lazy val algebraCourse = Course(new Id[Course](1), "Linear algebra", "Some description")
  lazy val philosophyCourse = Course(new Id[Course](2), "Philosophy", "Some description")

  lazy val user1 = User(new Id[User](1), "Ivan Kozlov", "i.kozlov@lms.ru", "", None)

  /**
   * Database table contents
   */
  lazy val coursesData = Seq(algebraCourse, philosophyCourse)
  lazy val usersData = Seq(user1)
}

object SampleDatabaseContent extends SampleDatabaseContent {
  import Schema._

  def fillDatabase(db: Database): Future[Unit] = {
    val insertOperations = DBIO.seq(
      courses ++= coursesData,
      users ++= usersData,
    )
    db.run(insertOperations)
  }
}
