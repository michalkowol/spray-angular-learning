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

  private var responseA = Option.empty[String]
  private var responseB = Option.empty[String]
  private var responseC = Option.empty[String]

  def receive = LoggingReceive {
    case GetResponseABC =>
      serviceA ! GetResponse
      serviceB ! GetResponse
      serviceC ! GetResponse
      context.become(waitingForResponses)
  }

  private def waitingForResponses = LoggingReceive {
    case ResponseA(a) =>
      responseA = Some(a)
      collectResults()
    case ResponseB(b) =>
      responseB = Some(b)
      collectResults()
    case ResponseC(c) =>
      responseC = Some(c)
      collectResults()
    case failure: Failure =>
      sendResponseAndShutdown(failure)
  }

  private def collectResults() = (responseA, responseB, responseC) match {
    case (Some(a), Some(b), Some(c)) =>
      sendResponseAndShutdown(ResponseABC(a, b, c))
    case _ =>
  }

  private def sendResponseAndShutdown(response: Any) = {
    originalSender ! response
    context.stop(self)
  }

  override def postStop {
    log.debug("Stopping...")
  }
}