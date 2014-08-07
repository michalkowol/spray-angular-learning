package pl.learning.sprayio.perrequest2

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import akka.event.LoggingReceive
import pl.learning.sprayio.perrequest2.PerRequest.{PerRequestWithFactory, PerRequestWithActorRef}
import pl.learning.sprayio._
import spray.httpx.Json4sSupport
import org.json4s.DefaultFormats
import spray.routing.RequestContext
import scala.language.postfixOps
import scala.concurrent.duration._
import spray.http.StatusCode
import spray.http.StatusCodes.{OK, BadRequest, GatewayTimeout, InternalServerError}

trait PerRequest extends Actor with ActorLogging with Json4sSupport {

  val ctx: RequestContext
  val target: ActorRef
  val message: RestMessage

  val json4sFormats = DefaultFormats

  context.setReceiveTimeout(250 milliseconds)
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
    def props(ctx: RequestContext, target: ActorRef, message: RestMessage) = Props(new PerRequestWithActorRef(ctx, target, message))
  }
  case class PerRequestWithActorRef(ctx: RequestContext, target: ActorRef, message: RestMessage) extends PerRequest

  object PerRequestWithFactory {
    def props(ctx: RequestContext, actorFactory: ActorFactory, message: RestMessage) = Props(new PerRequestWithFactory(ctx, actorFactory, message))
  }
  case class PerRequestWithFactory(ctx: RequestContext, actorFactory: ActorFactory, message: RestMessage) extends PerRequest {
    lazy val props = actorFactory.build(self)
    lazy val target = context.actorOf(props)
  }
}

trait PerRequestCreator {
  this: Actor =>

  def perRequest(ctx: RequestContext, target: ActorRef, message: RestMessage) = {
    context.actorOf(PerRequestWithActorRef.props(ctx, target, message))
  }

  def perRequest(ctx: RequestContext, actorFactory: ActorFactory, message: RestMessage) = {
    context.actorOf(PerRequestWithFactory.props(ctx, actorFactory, message))
  }
}
