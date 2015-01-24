package pl.learning.sprayio.cameo

import akka.actor.{ ActorLogging, Props, ActorRef, Actor }
import akka.event.LoggingReceive
import pl.learning.sprayio._
import pl.learning.sprayio.api.pattern.Cameo
import scala.concurrent.duration._

object CameoActor2 {
  def props(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef): Props =
    Props(new CameoActor2(originalSender, serviceA, serviceB, serviceC))
}

class CameoActor2(val originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging with Cameo {

  override def timeout: FiniteDuration = 200.milliseconds

  private var responseFromServiceA = Option.empty[String]
  private var responseFromServiceB = Option.empty[String]
  private var responseFromServiceC = Option.empty[String]

  def receive: Receive = LoggingReceive {
    case GetResponseABC =>
      serviceA ! GetResponse
      serviceB ! GetResponse
      serviceC ! GetResponse
      context.become(waitingForResponses)
  }

  private def waitingForResponses: Receive = LoggingReceive {
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

  private def collectResults: Unit = for {
    a <- responseFromServiceA
    b <- responseFromServiceB
    c <- responseFromServiceC
  } yield sendResponseAndShutdown(ResponseABC(a, b, c))
}
