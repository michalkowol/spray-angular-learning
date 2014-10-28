package pl.learning.sprayio.api.gathering

import akka.actor.Status.Failure
import akka.actor._
import akka.event.LoggingReceive
import pl.learning.sprayio._
import pl.learning.sprayio.api.PropsFactory

object GatheringActor {
  def props(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) = {
    Props(new GatheringActor(originalSender, serviceA, serviceB, serviceC))
  }
}

case class GatheringPropsFactory(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends PropsFactory {
  def props(originalSender: ActorRef) = GatheringActor.props(originalSender, serviceA, serviceB, serviceC)
}

class GatheringActor(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  var responseA = Option.empty[String]
  var responseB = Option.empty[String]
  var responseC = Option.empty[String]

  def receive = LoggingReceive {
    case GetResponseABC =>
      serviceA ! GetResponse
      serviceB ! GetResponse
      serviceC ! GetResponse
      context.become(waitingForResponses)
  }

  def waitingForResponses = LoggingReceive {
    case ResponseA(a) =>
      responseA = Some(a)
      collectResults()
    case ResponseB(b) =>
      responseB = Some(b)
      collectResults()
    case ResponseC(c) =>
      responseC = Some(c)
      collectResults()
    case Failure(e) =>
      throw e
  }

  def collectResults() = (responseA, responseB, responseC) match {
    case (Some(a), Some(b), Some(c)) =>
      originalSender ! ResponseABC(a, b, c)
      self ! PoisonPill
    case _ =>
  }

  override def postStop {
    log.debug("STOP STOP STOP STOP")
  }
}