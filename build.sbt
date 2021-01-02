name := "LMS"
version := "0.1"
scalaVersion := "2.13.3"

val AkkaVersion = "2.6.10"
val AkkaHttpVersion = "10.2.2"
val SlickVersion = "3.3.2"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % AkkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % AkkaVersion
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.0"
libraryDependencies += "com.typesafe.slick" %% "slick" % SlickVersion
libraryDependencies += "com.h2database" % "h2" % "1.4.196"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"
libraryDependencies += "de.heikoseeberger" %% "akka-http-play-json" % "1.31.0"
libraryDependencies += "io.scalaland" %% "chimney" % "0.6.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test

libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % Test
