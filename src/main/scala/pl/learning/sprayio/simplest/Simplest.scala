package pl.learning.sprayio.simplest

import akka.actor.{Props, ActorRef, ActorLogging, Actor}
import akka.event.LoggingReceive
import pl.learning.sprayio._

object Simplest {
  def props(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) = Props(new Simplest(serviceA, serviceB, serviceC))
}

class Simplest(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  var originalSender : ActorRef = _
  var responseA: Option[String] = None
  var responseB: Option[String] = None
  var responseC: Option[String] = None

  def receive = LoggingReceive {
    case GetResponseABC =>
      originalSender = sender // what if GetResponseABC are received several times? it will return value to wrong sender! (Cameo is much better)
      serviceA ! GetResponse
      serviceB ! GetResponse
      serviceC ! GetResponse
    case ResponseA(a) =>
      responseA = Some(a)
      collectResults()
    case ResponseB(b) =>
      responseB = Some(b)
      collectResults()
    case ResponseC(c) =>
      responseC = Some(c)
      collectResults()
  }

  def collectResults() = (responseA, responseB, responseC) match {
    case (Some(a), Some(b), Some(c)) =>
      responseA = None
      responseB = None
      responseC = None
      originalSender ! ResponseABC(a, b, c)
    case _ =>
  }
}
