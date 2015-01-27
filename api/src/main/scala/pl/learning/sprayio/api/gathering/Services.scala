package pl.learning.sprayio.api.gathering

import pl.learning.sprayio.api.pattern.NotifyOnError

import scala.util.Random
import scala.concurrent.duration._

import akka.actor.{ActorRef, Actor, ActorLogging}
import akka.event.LoggingReceive

case class ResponseABC(valueA: String, valueB: String, valueC: String)
case object GetResponseABC

case object GetResponse
case class ResponseA(value: String)
case class ResponseB(value: String)
case class ResponseC(value: String)

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

class NoResponseServiceB extends Actor with ActorLogging {

  def receive: Receive = LoggingReceive {
    case GetResponse => {
    }
  }
}

class InfiniteLoopServiceB extends Actor with ActorLogging {

  import context.dispatcher

  case class SendWaitAndRepeat(org: ActorRef)

  def receive: Receive = LoggingReceive {
    case GetResponse => {
      self ! SendWaitAndRepeat(sender())
    }
    case SendWaitAndRepeat(org) => {
      org ! ResponseB("has")
      context.system.scheduler.scheduleOnce(100.millisecond) {
        self ! SendWaitAndRepeat(org)
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
