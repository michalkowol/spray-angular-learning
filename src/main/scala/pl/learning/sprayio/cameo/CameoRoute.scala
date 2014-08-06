package pl.learning.sprayio.cameo

import akka.actor._
import akka.event.LoggingReceive
import pl.learning.sprayio._
import spray.routing.{RequestContext, Directives}

object ActorPerRequest {
  def props(ctx: RequestContext, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef): Props = Props(new ActorPerRequest(ctx, serviceA, serviceB, serviceC))
}

class ActorPerRequest(ctx: RequestContext, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponseABC =>
      val cameoDelegatingActor = context.actorOf(CameoDelegatingActor.props(serviceA, serviceB, serviceC))
      cameoDelegatingActor ! GetResponseABC
    case ResponseABC(a, b, c) =>
      ctx.complete(s"$a $b $c")
    case WorkTimeout =>
      ctx.failWith(new Exception("No response from Cameo"))
  }
}

class CameoRoute(implicit actorSystem: ActorSystem) extends Directives {

  val serviceA = actorSystem.actorOf(Props[ServiceA])
  val serviceB = actorSystem.actorOf(Props[ServiceB])
  val serviceC = actorSystem.actorOf(Props[ServiceC])

  lazy val route = {
    get {
      path("cameo") { ctx =>
        val actorPerRequest = actorSystem.actorOf(ActorPerRequest.props(ctx, serviceA, serviceB, serviceC))
        actorPerRequest ! GetResponseABC
      }
    }
  }
}
