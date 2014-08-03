package pl.learning.sprayio.cameo

import akka.actor.{ActorLogging, Actor}
import akka.event.LoggingReceive

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