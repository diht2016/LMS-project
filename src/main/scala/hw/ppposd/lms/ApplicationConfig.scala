package hw.ppposd.lms

import com.typesafe.config.{Config, ConfigFactory}

object ApplicationConfig {
  lazy val config: Config = ConfigFactory.defaultApplication()
  lazy val port: Int = config.getInt("server.port")
  lazy val interface: String = config.getString("server.interface")
}
