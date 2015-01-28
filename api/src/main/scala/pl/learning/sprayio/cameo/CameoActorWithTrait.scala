package pl.learning.sprayio.cameo

import scala.concurrent.duration._

import akka.actor.{ActorLogging, Props, ActorRef}
import akka.event.LoggingReceive

import pl.learning.sprayio.api.pattern.CameoActor
import pl.learning.sprayio.api.gathering._

object CameoActorWithTrait {
  def props(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef): Props =
    Props(new CameoActorWithTrait(originalSender, serviceA, serviceB, serviceC))
}

class CameoActorWithTrait(val originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends CameoActor with ActorLogging {

  override def timeout: FiniteDuration = 100.milliseconds

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
  } yield replyAndStop(ResponseABC(a, b, c))
}
