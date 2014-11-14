package pl.learning.sprayio.api

import akka.actor._
import akka.actor.Status.Failure
import akka.actor.SupervisorStrategy.Stop
import akka.event.LoggingReceive
import org.json4s.DefaultFormats
import pl.learning.sprayio.api.PerRequest.{ PerRequestWithPropsFactory, PerRequestWithActorRef }
import spray.http.StatusCode
import spray.http.StatusCodes.{ BadRequest, InternalServerError, OK, RequestTimeout }
import spray.httpx.Json4sSupport
import spray.routing.RequestContext

import scala.concurrent.duration._
import scala.util.control.NonFatal

trait PerRequest extends Actor with ActorLogging with Json4sSupport {

  def target: ActorRef

  val ctx: RequestContext
  val message: RestMessage
  val timeout: Duration

  val json4sFormats = DefaultFormats

  context.setReceiveTimeout(timeout)
  target ! message

  def receive = LoggingReceive {
    case response: RestMessage => complete(OK, response)
    case validation: ValidationError => complete(BadRequest, validation)
    case ReceiveTimeout => complete(RequestTimeout, RequestTimeoutError())
    case Failure(exception) => complete(InternalServerError, Error(exception))
  }

  def complete[T <: AnyRef](status: StatusCode, response: T) = {
    ctx.complete(status, response)
    self ! PoisonPill
  }

  override val supervisorStrategy = OneForOneStrategy() {
    case NonFatal(exception) =>
      complete(InternalServerError, Error(exception))
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
    def props = propsFactory.props(self)
    def target = context.actorOf(props)
  }
}

trait PerRequestCreator {
  implicit def actorRefFactory: ActorRefFactory

  def perRequest(ctx: RequestContext, target: ActorRef, message: RestMessage, timeout: Duration = 250.milliseconds) = {
    actorRefFactory.actorOf(PerRequestWithActorRef.props(ctx, target, message, timeout))
  }

  def perRequestWithFactory(ctx: RequestContext, propsFactory: PropsFactory, message: RestMessage, timeout: Duration = 250.milliseconds) = {
    actorRefFactory.actorOf(PerRequestWithPropsFactory.props(ctx, propsFactory, message, timeout))
  }
}