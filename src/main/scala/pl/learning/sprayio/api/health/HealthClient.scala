package pl.learning.sprayio.api.health

import akka.actor.Actor
import akka.event.LoggingReceive

class HealthClient extends Actor {
  def receive = LoggingReceive {
    case GetHealth => sender ! Health("up")
  }
}
