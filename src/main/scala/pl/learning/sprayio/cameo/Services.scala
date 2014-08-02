package pl.learning.sprayio.cameo

import akka.actor.{ActorLogging, Actor}
import akka.event.LoggingReceive

class ServiceA extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
      Thread.sleep(100)
      println("sending response from A")
      sender ! ResponseA("response from A")
    }
  }
}

class ServiceB extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponse => {
      Thread.sleep(200)
      println("sending response from B")
      sender ! ResponseB("response from B")
    }
  }
}