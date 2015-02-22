package pl.learning.sprayio.api.pattern

import akka.actor.Status.Failure
import akka.actor.SupervisorStrategy.{Decider, Stop}
import akka.actor._
import akka.event.LoggingReceive
import com.paypal.cascade.akka.actor.ServiceActor
import pl.learning.sprayio.TimeoutException
import pl.learning.sprayio.api.{NotFound, Error, Validation}
import pl.learning.sprayio.marshallers._
import spray.http.StatusCode
import spray.http.StatusCodes.{BadRequest, InternalServerError, OK, RequestTimeout, NotFound => NotFoundCode}
import spray.routing.RequestContext

import scala.concurrent.duration._
import scala.util.control.NonFatal

object PerRequest {
  object PerRequestWithActorRef {
    def props(ctx: RequestContext, target: ActorRef, message: Any, timeout: Duration): Props =
      Props(new PerRequestWithActorRef(ctx, target, message, timeout))
  }
  case class PerRequestWithActorRef(ctx: RequestContext, target: ActorRef, message: Any, timeout: Duration) extends PerRequest

  object PerRequestWithActorRefMaker {
    def props(ctx: RequestContext, actorRefMaker: ActorRefMaker, message: Any, timeout: Duration): Props =
      Props(new PerRequestWithActorRefMaker(ctx, actorRefMaker, message, timeout))
  }
  case class PerRequestWithActorRefMaker(ctx: RequestContext, actorRefMaker: ActorRefMaker, message: Any, timeout: Duration) extends PerRequest {
    lazy val target = actorRefMaker(context, self)
  }
}

trait PerRequest extends ServiceActor {

  val ctx: RequestContext
  val target: ActorRef
  val message: Any
  val timeout: Duration

  context.setReceiveTimeout(timeout)
  target ! message

  def receive: Receive = LoggingReceive {
    case validation: Validation => complete(BadRequest, validation)
    case notFound: NotFound => complete(NotFoundCode, notFound)
    case Failure(exception) => complete(InternalServerError, Error(exception))
    case ReceiveTimeout => complete(RequestTimeout, Error(new TimeoutException))
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

  def perRequest(ctx: RequestContext, target: ActorRef, message: Any): ActorRef = {
    perRequest(ctx, target, message, 500.milliseconds)
  }

  def perRequest(ctx: RequestContext, target: ActorRef, message: Any, timeout: Duration): ActorRef = {
    actorRefFactory.actorOf(PerRequest.PerRequestWithActorRef.props(ctx, target, message, timeout))
  }

  def perRequest(ctx: RequestContext, actorRefMaker: ActorRefMaker, message: Any): ActorRef = {
    perRequest(ctx, actorRefMaker, message, 500.milliseconds)
  }

  def perRequest(ctx: RequestContext, actorRefMaker: ActorRefMaker, message: Any, timeout: Duration): ActorRef = {
    actorRefFactory.actorOf(PerRequest.PerRequestWithActorRefMaker.props(ctx, actorRefMaker, message, timeout))
  }
}
