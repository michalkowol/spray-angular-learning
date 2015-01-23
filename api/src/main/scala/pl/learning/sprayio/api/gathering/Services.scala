package pl.learning.sprayio.api.gathering

import pl.learning.sprayio.api.pattern.NotifyOnError

import scala.util.Random

import akka.actor.Status.Failure
import akka.actor.{ Actor, ActorLogging }
import akka.event.LoggingReceive

class ServiceA extends Actor with ActorLogging {
  def receive: Receive = LoggingReceive {
    case GetResponse =>
      sender() ! ResponseA("John")
  }
}

class ServiceB extends Actor with ActorLogging {
  def receive: Receive = LoggingReceive {
    case GetResponse =>
      sender() ! ResponseB("has")
  }
}

class RandomServiceB extends Actor with ActorLogging {

  def receive: Receive = LoggingReceive {
    case GetResponse => NotifyOnError {
      Random.nextInt(3) match {
        case 0 => throw new Exception("Custom exception")
        case 1 => // WARNING: Chrome repeats Request Timeout (408)
        case _ => sender() ! ResponseB("has")
      }
    }
  }
}

class ServiceC extends Actor with ActorLogging {
  def receive: Receive = LoggingReceive {
    case GetResponse =>
      sender() ! ResponseC("cat")
  }
}
