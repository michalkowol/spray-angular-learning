package pl.learning.sprayio.cameo

import akka.actor._
import akka.event.LoggingReceive
import pl.learning.sprayio._
import scala.concurrent.duration._
import scala.language.postfixOps

object Cameo2Actor {
  def props(originalSender: ActorRef) = Props(new Cameo2Actor(originalSender))
}

class Cameo2Actor(originalSender: ActorRef) extends Actor with ActorLogging {

  var responseFromServiceA: Option[String] = None
  var responseFromServiceB: Option[String] = None
  var responseFromServiceC: Option[String] = None

  context.setReceiveTimeout(250 milliseconds) // could not send ReceiveTimeout - example: service A is sending ResponseA in loop and service B is not sending response at all

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
    case ReceiveTimeout  =>
      sendResponseAndShutdown(WorkTimeout)
  }

  def collectResults() = (responseFromServiceA, responseFromServiceB, responseFromServiceC) match {
    case (Some(a), Some(b), Some(c)) =>
      sendResponseAndShutdown(ResponseABC(a, b, c))
    case _ =>
  }

  def sendResponseAndShutdown(response: Any) = {
    originalSender ! response
    self ! PoisonPill
  }
}

object Cameo2DelegatingActor {
  def props(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) = Props(new Cameo2DelegatingActor(serviceA, serviceB, serviceC))
}

class Cameo2DelegatingActor(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponseABC =>
      val originalSender = sender
      val cameoWorker = context.actorOf(Cameo2Actor.props(originalSender))
      serviceA.tell(GetResponse, cameoWorker)
      serviceB.tell(GetResponse, cameoWorker)
      serviceC.tell(GetResponse, cameoWorker)
  }
}