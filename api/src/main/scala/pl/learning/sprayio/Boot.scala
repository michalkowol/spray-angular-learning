package pl.learning.sprayio

import akka.actor.{Props, ActorSystem}
import akka.io.IO
import spray.can.Http
import spray.can.server.ServerSettings

object Boot {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("api-system")
    val api = system.actorOf(Props[Api], "api")

    IO(Http) ! Http.Bind(api, "0.0.0.0", port = 8080)
  }
}
