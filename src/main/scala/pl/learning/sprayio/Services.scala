package pl.learning.sprayio

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive

trait ServicesMessages
case object GetResponse
case class ResponseA(value: String) extends ServicesMessages
case class ResponseB(value: String) extends ServicesMessages
case class ResponseC(value: String) extends ServicesMessages

class ServiceA extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
      Thread.sleep(100)
      log.debug("sending response from A")
      sender ! ResponseA("Ala")
    }
  }
}

class ServiceB extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
      Thread.sleep(200)
      log.debug("sending response from B")
      sender ! ResponseB("ma")
    }
  }
}

class ServiceC extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
      Thread.sleep(100)
      log.debug("sending response from C")
      sender ! ResponseC("kota")
    }
  }
}