package pl.learning.sprayio.api.sameactorproblem

import akka.actor.{ActorRefFactory, Props, ActorRef}
import akka.event.LoggingReceive
import com.paypal.cascade.akka.actor.ServiceActor
import com.paypal.cascade.common.option._
import pl.learning.sprayio.api.gathering.MessageProvider
import pl.learning.sprayio.api.pattern.ActorRefMaker

case class AggregatorRefMaker(service: ActorRef) extends ActorRefMaker {
  override def create(context: ActorRefFactory, originalSender: ActorRef): ActorRef = {
    context.actorOf(Aggregator.props(originalSender, service), "Aggregator")
  }
}

object Aggregator {
  case object Aggregate
  def props(originalSender: ActorRef, service: ActorRef): Props = Props(new Aggregator(originalSender, service))
}

class Aggregator(val originalSender: ActorRef, service: ActorRef) extends ServiceActor {

  import Aggregator._

  private var a = Option.empty[Int]
  private var b = Option.empty[Int]

  override def receive: Receive = LoggingReceive {
    case Aggregate =>
      service ! MessageProvider.Identifiable("a", 6)
      service ! MessageProvider.Identifiable("b", 2)
      context.become(waitForResponses)
  }

  private def waitForResponses: Receive = LoggingReceive {
    case MessageProvider.Identifiable("a", a: Int) =>
      this.a = a.some
      collectResults()
    case MessageProvider.Identifiable("b", b: Int) =>
      this.b = b.some
      collectResults()
  }

  private def collectResults(): Unit = (a, b) match {
    case (Some(a), Some(b)) => originalSender ! a + b
    case _ =>
  }
}
