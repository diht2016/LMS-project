package hw.ppposd.lms

import akka.actor.ActorSystem
import akka.http.scaladsl.Http

import scala.concurrent.ExecutionContext
import scala.io.StdIn

object Application extends App {
  implicit val system: ActorSystem = ActorSystem("lms-system")
  implicit val ec: ExecutionContext = system.dispatcher

  Schema.createSchema()

  val route = new RootRouting(new Wiring).route

  val port = 8080 // todo: move to config
  val binding = Http().newServerAt("localhost", port).bind(route)

  println(s"Server running at http://localhost:$port/")
  println("Press RETURN to stop")
  StdIn.readLine()
  binding.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
