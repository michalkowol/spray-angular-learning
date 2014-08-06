package pl.learning.sprayio.perrequest

import akka.actor._
import akka.event.LoggingReceive
import pl.learning.sprayio._
import spray.routing.{RequestContext, Directives}
import scala.language.postfixOps
import scala.concurrent.duration._

object GatheringActor {
  def props(originalSender : ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) = {
    Props(new GatheringActor(originalSender, serviceA, serviceB, serviceC))
  }
}

class GatheringActor(originalSender : ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  var responseA: Option[String] = None
  var responseB: Option[String] = None
  var responseC: Option[String] = None

  def receive = LoggingReceive {
    case GetResponseABC =>
      serviceA ! GetResponse
      serviceB ! GetResponse
      serviceC ! GetResponse
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
}

object PerRequestPattern {
  def props(ctx: RequestContext) = Props(new PerRequestPattern(ctx))
}

class PerRequestPattern(ctx: RequestContext) extends Actor with ActorLogging {

  context.setReceiveTimeout(250 milliseconds)

  def receive = LoggingReceive {
    case GetResponseABC =>
      val serviceA = context.actorOf(Props[ServiceA])
      val serviceB = context.actorOf(Props[ServiceB])
      val serviceC = context.actorOf(Props[ServiceC])
      val gatheringActor = context.actorOf(GatheringActor.props(self, serviceA, serviceB, serviceC))
      gatheringActor ! GetResponseABC
    case ResponseABC(a, b, c) =>
      ctx.complete(s"$a $b $c")
      self ! PoisonPill
    case ReceiveTimeout =>
      ctx.complete("Timeout")
      self ! PoisonPill
  }
}

class PerRequestRoute(implicit system: ActorSystem) extends Directives {

  lazy val route = {
    path("perrequest") {
      get { ctx =>
        val perRequestPattern = system.actorOf(PerRequestPattern.props(ctx))
        perRequestPattern ! GetResponseABC
      }
    }
  }
}
