package pl.learning.sprayio.api.gathering

import akka.actor._
import akka.event.LoggingReceive
import com.paypal.cascade.akka.actor.ServiceActor
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

class GatheringActor(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends ServiceActor {

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
      collectResults()
    case ResponseB(b) =>
      responseB = Some(b)
      collectResults()
    case ResponseC(c) =>
      responseC = Some(c)
      collectResults()
  }

  private def collectResults(): Unit = (responseA, responseB, responseC) match {
    case (Some(a), Some(b), Some(c)) => originalSender ! ResponseABC(a, b, c)
    case _ =>
  }

  override def postStop: Unit = {
    log.debug("Stopping...")
  }
}
