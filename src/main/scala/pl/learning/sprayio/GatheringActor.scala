package pl.learning.sprayio

import akka.actor.SupervisorStrategy.Escalate
import akka.actor._
import akka.event.LoggingReceive

object GatheringActor {
  def props(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) = {
    Props(new GatheringActor(originalSender, serviceA, serviceB, serviceC))
  }
}

case class GatheringPropsFactory(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends PropsFactory {
  def build(originalSender: ActorRef) = GatheringActor.props(originalSender, serviceA, serviceB, serviceC)
}

class GatheringActor(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  var responseA: Option[String] = None
  var responseB: Option[String] = None
  var responseC: Option[String] = None

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
  }

  def collectResults() = (responseA, responseB, responseC) match {
    case (Some(a), Some(b), Some(c)) =>
      originalSender ! ResponseABC(a, b, c)
      self ! PoisonPill
    case _ =>
  }

  override val supervisorStrategy = OneForOneStrategy() {
    case _ => Escalate
  }
}