package pl.learning.sprayio.api.health

import akka.actor.Actor
import akka.event.LoggingReceive

object HealthActor {
  case class Health(status: String)
  case object GetHealth
}

class HealthActor extends Actor {
  import HealthActor._
  def receive: Receive = LoggingReceive {
    case GetHealth => sender() ! Health("up")
  }
}
