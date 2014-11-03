package pl.learning.sprayio.api.gathering

import akka.actor.{ Actor, ActorLogging }
import akka.event.LoggingReceive
import pl.learning.sprayio.api.NotifyOnError

class ServiceA extends Actor with ActorLogging {
  def receive = LoggingReceive {
    case GetResponse =>
      sender ! ResponseA("John")
  }
}

class ServiceB extends Actor with ActorLogging {
  def receive = LoggingReceive {
    case GetResponse => NotifyOnError {
      sender ! ResponseB("has")
      //throw new Exception("foo bar baz")
    }
  }
}

class ServiceC extends Actor with ActorLogging {
  def receive = LoggingReceive {
    case GetResponse =>
      sender ! ResponseC("cat")
  }
}