package pl.learning.sprayio.perrequest

import akka.actor._
import akka.event.LoggingReceive
import pl.learning.sprayio._
import pl.learning.sprayio.api.gathering.GatheringActor
import spray.routing.{ RequestContext, Directives }
import scala.concurrent.duration._

object PerRequestPattern {
  def props(ctx: RequestContext) = Props(new PerRequestPattern(ctx))
}

class PerRequestPattern(ctx: RequestContext) extends Actor with ActorLogging {

  context.setReceiveTimeout(250.milliseconds)

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
