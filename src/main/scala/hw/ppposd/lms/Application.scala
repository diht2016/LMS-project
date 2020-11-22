package hw.ppposd.lms

import hw.ppposd.lms.course.{Course, CourseRepository, CourseRepositoryImpl}
import hw.ppposd.lms.util.Id

import scala.concurrent.ExecutionContext.Implicits.global
//import akka.actor.ActorSystem

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object Application extends App {
  //implicit val system: ActorSystem = ActorSystem("rest-service-example")

  val c: CourseRepository = new CourseRepositoryImpl

  def test(f: Future[_]): Unit = Await.ready(f.map(println), Duration.Inf)

  Thread.sleep(3000)

  test(c.create("first course", "test"))
  Thread.sleep(500)
  test(c.create("second course", "one more test"))

  Thread.sleep(2000)

  test(c.find(new Id[Course](-1)))
  Thread.sleep(500)
  test(c.find(new Id[Course](0)))
  Thread.sleep(500)
  test(c.find(new Id[Course](1)))
  Thread.sleep(500)
  test(c.find(new Id[Course](2)))
}
