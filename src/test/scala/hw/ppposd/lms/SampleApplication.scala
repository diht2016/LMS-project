package hw.ppposd.lms

/**
 * Application with sample data in its database
 * Use `test:run` sbt command to launch it
 */
object SampleApplication extends App {
  TestDatabase.initializeDatabase()

  Application.main(Array())
}
