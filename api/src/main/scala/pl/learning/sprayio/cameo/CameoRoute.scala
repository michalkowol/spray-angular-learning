package pl.learning.sprayio.cameo

import akka.actor.Status.Failure
import akka.actor._
import akka.event.LoggingReceive
import pl.learning.sprayio._
import spray.routing.{ HttpService, RequestContext, Directives }

object ActorPerRequest {
  def props(ctx: RequestContext, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef): Props =
    Props(new ActorPerRequest(ctx, serviceA, serviceB, serviceC))
}

class ActorPerRequest(ctx: RequestContext, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponseABC =>
      val cameo = context.actorOf(CameoActor2.props(self, serviceA, serviceB, serviceC))
      cameo ! GetResponseABC
    case ResponseABC(a, b, c) =>
      ctx.complete(s"$a $b $c")
    case Failure(e) =>
      ctx.failWith(e)
  }
}

trait CameoRoute extends HttpService {

  val serviceA = actorRefFactory.actorOf(Props[ServiceA])
  val serviceB = actorRefFactory.actorOf(Props[ServiceB])
  val serviceC = actorRefFactory.actorOf(Props[ServiceC])

  lazy val cameoRoute = {
    get {
      path("cameo") { ctx =>
        val actorPerRequest = actorRefFactory.actorOf(ActorPerRequest.props(ctx, serviceA, serviceB, serviceC))
        actorPerRequest ! GetResponseABC
      }
    }
  }
}
