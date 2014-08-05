package pl.learning.sprayio.cameo

import akka.actor.{ActorLogging, Props, ActorRef, Actor}
import akka.event.LoggingReceive
import pl.learning.sprayio._
import scala.language.postfixOps

object CameoWithoutTimeoutActor {
  def props(originalSender: ActorRef) = Props(new CameoActor(originalSender))
}

class CameoWithoutTimeoutActor(originalSender: ActorRef) extends Actor with ActorLogging {

  var responseFromServiceA: Option[String] = None
  var responseFromServiceB: Option[String] = None
  var responseFromServiceC: Option[String] = None

  def receive = LoggingReceive {
    case ResponseA(value) =>
      responseFromServiceA = Some(value)
      collectResults()
    case ResponseB(value) =>
      responseFromServiceB = Some(value)
      collectResults()
    case ResponseC(value) =>
      responseFromServiceC = Some(value)
      collectResults()
  }

  def collectResults() = (responseFromServiceA, responseFromServiceB, responseFromServiceC) match {
    case (Some(a), Some(b), Some(c)) =>
      originalSender ! ResponseABC(a, b, c)
      context.stop(self)
    case _ =>
  }
}

object CameoWithoutTimeoutDelegatingActor {
  def props(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) = Props(new CameoWithoutTimeoutDelegatingActor(serviceA, serviceB, serviceC))
}

class CameoWithoutTimeoutDelegatingActor(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponseABC =>
      val originalSender = sender
      val cameoWorker = context.actorOf(CameoWithoutTimeoutActor.props(originalSender))
      serviceA.tell(GetResponse, cameoWorker)
      serviceB.tell(GetResponse, cameoWorker)
      serviceC.tell(GetResponse, cameoWorker)
  }
}