package pl.learning.sprayio

import akka.actor.SupervisorStrategy.Escalate
import akka.actor._
import akka.event.LoggingReceive
import pl.learning.sprayio.perrequest2._

object GatheringActor {
  def props(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) = {
    Props(new GatheringActor(originalSender, serviceA, serviceB, serviceC))
  }
}

case class GatheringPropsFactory(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends PropsFactory {
  def build(originalSender: ActorRef) = GatheringActor.props(originalSender, serviceA, serviceB, serviceC)
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
    case e: Error =>
      throw FooException
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