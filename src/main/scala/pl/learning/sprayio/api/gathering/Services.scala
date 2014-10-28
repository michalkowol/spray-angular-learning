package pl.learning.sprayio.api.gathering

import akka.actor.{ Actor, ActorLogging}
import akka.event.LoggingReceive

class ServiceA extends Actor with ActorLogging {
  def receive = LoggingReceive {
    case GetResponse => {
      sender ! ResponseA("John")
    }
  }
}

class ServiceB extends Actor with ActorLogging {
  def receive = LoggingReceive {
    case GetResponse => {
      sender ! ResponseB("has")
    }
  }
}

class ServiceC extends Actor with ActorLogging {
  def receive = LoggingReceive {
    case GetResponse => {
      sender ! ResponseC("cat")
    }
  }
}