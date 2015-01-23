package pl.learning.sprayio.api.pattern

import akka.actor.Status.Failure
import akka.actor.SupervisorStrategy.{Decider, Stop}
import akka.actor._
import akka.event.LoggingReceive
import org.json4s.DefaultFormats
import pl.learning.sprayio.TimeoutException
import pl.learning.sprayio.api.{Error, Validation}
import spray.http.StatusCode
import spray.http.StatusCodes.{BadRequest, InternalServerError, OK, RequestTimeout}
import spray.httpx.Json4sSupport
import spray.routing.RequestContext

import scala.concurrent.duration._
import scala.util.control.NonFatal

object PerRequest {
  object PerRequestWithActorRef {
    def props(ctx: RequestContext, target: ActorRef, message: Any, timeout: Duration): Props =
      Props(new PerRequestWithActorRef(ctx, target, message, timeout))
  }
  case class PerRequestWithActorRef(ctx: RequestContext, target: ActorRef, message: Any, timeout: Duration) extends PerRequest

  object PerRequestWithPropsFactory {
    def props(ctx: RequestContext, propsFactory: PropsFactory, message: Any, timeout: Duration): Props =
      Props(new PerRequestWithPropsFactory(ctx, propsFactory, message, timeout))
  }
  case class PerRequestWithPropsFactory(ctx: RequestContext, propsFactory: PropsFactory, message: Any, timeout: Duration) extends PerRequest {
    lazy val props = propsFactory.props(self)
    lazy val target = context.actorOf(props)
  }
}

trait PerRequest extends Actor with ActorLogging with Json4sSupport {

  val ctx: RequestContext
  val target: ActorRef
  val message: Any
  val timeout: Duration

  val json4sFormats = DefaultFormats

  context.setReceiveTimeout(timeout)
  target ! message

  def receive: Receive =  LoggingReceive {
    case validation: Validation => complete(BadRequest, validation)
    case Failure(exception) => complete(InternalServerError, Error(exception.getMessage))
    case ReceiveTimeout => complete(RequestTimeout, new TimeoutException)
    case response: AnyRef => complete(OK, response)
  }

  def complete[T <: AnyRef](status: StatusCode, response: T): Unit = {
    ctx.complete(status, response)
    context.stop(self)
  }

  override val supervisorStrategy = OneForOneStrategy() {
    val stopOnNonFatal: Decider = {
      case NonFatal(exception) =>
        complete(InternalServerError, Error(exception.getMessage))
        Stop
    }
    stopOnNonFatal orElse SupervisorStrategy.defaultDecider
  }
}

trait PerRequestCreator {
  implicit def actorRefFactory: ActorRefFactory

  def perRequestWithRef(ctx: RequestContext, target: ActorRef, message: Any, timeout: Duration = 500.milliseconds): ActorRef = {
    actorRefFactory.actorOf(PerRequest.PerRequestWithActorRef.props(ctx, target, message, timeout))
  }

  def perRequest(ctx: RequestContext, propsFactory: PropsFactory, message: Any, timeout: Duration = 500.milliseconds): ActorRef = {
    actorRefFactory.actorOf(PerRequest.PerRequestWithPropsFactory.props(ctx, propsFactory, message, timeout))
  }
}
