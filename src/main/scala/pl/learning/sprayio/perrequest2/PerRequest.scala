package pl.learning.sprayio.perrequest2

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import akka.event.LoggingReceive
import pl.learning.sprayio.perrequest2.PerRequest.{ PerRequestWithPropsFactory, PerRequestWithActorRef }
import pl.learning.sprayio._
import spray.httpx.Json4sSupport
import org.json4s.DefaultFormats
import spray.routing.RequestContext
import scala.concurrent.duration._
import scala.language.postfixOps
import spray.http.StatusCode
import spray.http.StatusCodes.{ OK, BadRequest, GatewayTimeout, InternalServerError }

trait PerRequest extends Actor with ActorLogging with Json4sSupport {

  val ctx: RequestContext
  val target: ActorRef
  val message: RestMessage
  val timeout: Duration

  val json4sFormats = DefaultFormats

  context.setReceiveTimeout(timeout)
  target ! message

  def receive = LoggingReceive {
    case response: RestMessage => complete(OK, response)
    case validation: Validation => complete(BadRequest, validation)
    case ReceiveTimeout => complete(GatewayTimeout, Error("Request timeout"))
  }

  def complete[T <: AnyRef](status: StatusCode, response: T) = {
    ctx.complete(status, response)
    self ! PoisonPill
  }

  override val supervisorStrategy = OneForOneStrategy() {
    case exception =>
      complete(InternalServerError, Error(exception.getMessage))
      Stop
  }
}

object PerRequest {
  object PerRequestWithActorRef {
    def props(ctx: RequestContext, target: ActorRef, message: RestMessage, timeout: Duration) = Props(new PerRequestWithActorRef(ctx, target, message, timeout))
  }
  case class PerRequestWithActorRef(ctx: RequestContext, target: ActorRef, message: RestMessage, timeout: Duration) extends PerRequest

  object PerRequestWithPropsFactory {
    def props(ctx: RequestContext, propsFactory: PropsFactory, message: RestMessage, timeout: Duration) = Props(new PerRequestWithPropsFactory(ctx, propsFactory, message, timeout))
  }
  case class PerRequestWithPropsFactory(ctx: RequestContext, propsFactory: PropsFactory, message: RestMessage, timeout: Duration) extends PerRequest {
    lazy val props = propsFactory.build(self)
    lazy val target = context.actorOf(props)
  }
}

trait PerRequestCreator {
  this: Actor =>

  def perRequestWithActorRef(ctx: RequestContext, target: ActorRef, message: RestMessage, timeout: Duration = 250 milliseconds) = {
    context.actorOf(PerRequestWithActorRef.props(ctx, target, message, timeout))
  }

  def perRequest(ctx: RequestContext, propsFactory: PropsFactory, message: RestMessage, timeout: Duration = 250 milliseconds) = {
    context.actorOf(PerRequestWithPropsFactory.props(ctx, propsFactory, message, timeout))
  }
}
