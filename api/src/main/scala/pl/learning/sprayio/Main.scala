package pl.learning.sprayio

import akka.actor.{ Props, ActorSystem }
import akka.io.IO
import spray.can.Http
import spray.can.server.ServerSettings

object Main extends App with CustomSslConfiguration {
  implicit val system = ActorSystem("api-system")
  val api = system.actorOf(Props[Api], "api")
  val settings = ServerSettings(system)

  IO(Http) ! Http.Bind(api, "0.0.0.0", port = 8080)
  IO(Http) ! Http.Bind(api, "0.0.0.0", port = 8443, settings = Some(settings.copy(sslEncryption = true)))
}
