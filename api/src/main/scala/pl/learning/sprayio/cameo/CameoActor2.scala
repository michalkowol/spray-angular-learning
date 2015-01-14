package pl.learning.sprayio.cameo

import akka.actor.{ ActorLogging, Props, ActorRef, Actor }
import akka.event.LoggingReceive
import pl.learning.sprayio._
import scala.concurrent.duration._

object CameoActor2 {
  def props(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) = Props(new CameoActor2(originalSender, serviceA, serviceB, serviceC))
}

class CameoActor2(val originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging with Cameo {

  override def timeout = 200.milliseconds

  var responseFromServiceA = Option.empty[String]
  var responseFromServiceB = Option.empty[String]
  var responseFromServiceC = Option.empty[String]

  def receive = LoggingReceive {
    case GetResponseABC =>
      serviceA ! GetResponse
      serviceB ! GetResponse
      serviceC ! GetResponse
      context.become(waitingForResponses)
  }

  def waitingForResponses: Receive = LoggingReceive {
    case ResponseA(value) =>
      responseFromServiceA = Some(value)
      collectResults
    case ResponseB(value) =>
      responseFromServiceB = Some(value)
      collectResults
    case ResponseC(value) =>
      responseFromServiceC = Some(value)
      collectResults
  }

  def collectResults: Unit = for {
    a <- responseFromServiceA
    b <- responseFromServiceB
    c <- responseFromServiceC
  } yield sendResponseAndShutdown(ResponseABC(a, b, c))
}