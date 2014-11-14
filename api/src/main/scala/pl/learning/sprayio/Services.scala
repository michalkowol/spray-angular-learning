package pl.learning.sprayio

import akka.actor.{ ActorRef, Actor, ActorLogging }
import akka.event.LoggingReceive
import scala.concurrent.duration._

import scala.util.{ Failure, Random }

trait ServicesMessages
case object GetResponse
case class ResponseA(value: String) extends ServicesMessages
case class ResponseB(value: String) extends ServicesMessages
case class ResponseC(value: String) extends ServicesMessages

class InfiniteLoopServiceA extends Actor with ActorLogging {

  import context.dispatcher

  case class SendWaitAndRepeat(org: ActorRef)

  def receive = LoggingReceive {
    case GetResponse => {
      self ! SendWaitAndRepeat(sender)
    }
    case SendWaitAndRepeat(org) => {
      org ! ResponseA("Ala")
      context.system.scheduler.scheduleOnce(100.millisecond) {
        self ! SendWaitAndRepeat(org)
      }
    }
  }
}

class ServiceA extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
      sender ! ResponseA("Ala")
    }
  }
}

class ServiceB extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
      sender ! ResponseB("ma")
    }
  }
}

class RandomServiceB extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
      Random.nextInt(4) match {
        case 0 => sender ! Failure(new Exception("Foo"))
        case 1 =>
        case _ => sender ! ResponseB("ma")
      }
    }
  }
}

class NoResponseServiceB extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
    }
  }
}

class ServiceC extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
      sender ! ResponseC("kota")
    }
  }
}