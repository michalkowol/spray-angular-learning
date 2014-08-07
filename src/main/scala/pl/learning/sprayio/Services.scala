package pl.learning.sprayio

import akka.actor.{ ActorRef, Actor, ActorLogging }
import akka.event.LoggingReceive

import scala.util.Random

trait ServicesMessages
case object GetResponse
case class ResponseA(value: String) extends ServicesMessages
case class ResponseB(value: String) extends ServicesMessages
case class ResponseC(value: String) extends ServicesMessages

class InfiniteLoopServiceA extends Actor with ActorLogging {

  case class SendWaitAndRepeat(org: ActorRef)

  def receive = LoggingReceive {
    case GetResponse => {
      self ! SendWaitAndRepeat(sender)
    }
    case SendWaitAndRepeat(org) => {
      org ! ResponseA("Ala")
      Thread.sleep(100)
      self ! SendWaitAndRepeat(org)
    }
  }
}

class ServiceA extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
      Thread.sleep(200)
      sender ! ResponseA("Ala")
    }
  }
}

class ServiceB extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
      Thread.sleep(200)
      sender ! ResponseB("ma")
    }
  }
}

class RandomServiceB extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
      Thread.sleep(200)
      Random.nextInt(3) match {
        case 0 => sender ! ResponseB("ma")
        case 1 => sender ! Error("Foo")
        case 2 =>
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
      Thread.sleep(100)
      sender ! ResponseC("kota")
    }
  }
}