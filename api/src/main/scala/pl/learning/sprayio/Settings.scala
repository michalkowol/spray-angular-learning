package pl.learning.sprayio

import com.typesafe.config.ConfigFactory

object Settings {
  private val config = ConfigFactory.load()

  val port: Int = {
    val portBase = getOptionInt("server.port.port").getOrElse(8080)
    val portOffset = getOptionInt("server.port.offset").getOrElse(0)
    portBase + portOffset
  }

  def getOptionInt(path: String): Option[Int] = if (config.hasPath(path)) {
    Option(config.getInt(path))
  } else {
    None
  }
}
