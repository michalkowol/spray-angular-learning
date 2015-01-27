package pl.learning.sprayio.cameo

import akka.actor.Status.Failure
import akka.actor._
import akka.event.LoggingReceive
import spray.routing.{ HttpService, RequestContext }

import pl.learning.sprayio.api.gathering._

object ActorPerRequest {
  def props(ctx: RequestContext, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef): Props =
    Props(new ActorPerRequest(ctx, serviceA, serviceB, serviceC))
}

class ActorPerRequest(ctx: RequestContext, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  def receive: Receive = LoggingReceive {
    case GetResponseABC =>
      val cameo = context.actorOf(CameoActorWithTrait.props(self, serviceA, serviceB, serviceC))
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
