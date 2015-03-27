package pl.learning.sprayio

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.paypal.cascade.akka.actor.ServiceActor
import spray.can.Http

object Boot {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("api-system")
    val api = system.actorOf(Props[Api], "api")
    val bindListener = system.actorOf(Props[BindListener])
    val bind = Http.Bind(api, "0.0.0.0", port = Settings.port)
    IO(Http).tell(bind, bindListener)
  }
}

class BindListener extends ServiceActor {
  override def receive: Receive = {
    case bound: Http.Bound =>
      log.info("{}", bound)
      context.stop(self)
    case error =>
      log.error("{}", error)
      context.system.shutdown()
  }
}
