package pl.learning.sprayio.api.gathering

import akka.actor.Status.Failure
import akka.actor._
import akka.event.LoggingReceive
import pl.learning.sprayio._
import pl.learning.sprayio.api.pattern.ActorRefMaker

object GatheringActor {
  def props(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef): Props = {
    Props(new GatheringActor(originalSender, serviceA, serviceB, serviceC))
  }
}

case class GatheringActorRefMaker(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends ActorRefMaker {
  override def create(context: ActorRefFactory, originalSender: ActorRef): ActorRef = {
    context.actorOf(GatheringActor.props(originalSender, serviceA, serviceB, serviceC))
  }
}

class GatheringActor(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  private var responseA = Option.empty[String]
  private var responseB = Option.empty[String]
  private var responseC = Option.empty[String]

  def receive: Receive = LoggingReceive {
    case GetResponseABC =>
      serviceA ! GetResponse
      serviceB ! GetResponse
      serviceC ! GetResponse
      context.become(waitingForResponses)
  }

  private def waitingForResponses = LoggingReceive {
    case ResponseA(a) =>
      responseA = Some(a)
      collectResults
    case ResponseB(b) =>
      responseB = Some(b)
      collectResults
    case ResponseC(c) =>
      responseC = Some(c)
      collectResults
    case failure: Failure =>
      sendResponseAndShutdown(failure)
  }

  private def collectResults = for {
    a <- responseA
    b <- responseB
    c <- responseC
  } yield sendResponseAndShutdown(ResponseABC(a, b, c))

  private def sendResponseAndShutdown(response: Any) = {
    originalSender ! response
    context.stop(self)
  }

  override def postStop: Unit = {
    log.debug("Stopping...")
  }
}
