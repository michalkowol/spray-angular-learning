package pl.learning.sprayio.api.pattern

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorRef}
import akka.event.LoggingReceive
import pl.learning.sprayio.{MessageNotSupported, TimeoutException}

import scala.concurrent.duration._

trait CameoActor extends Actor {

  def originalSender: ActorRef
  def timeout: FiniteDuration = 250.milliseconds

  def replyAndStop(response: Any): Unit = {
    originalSender ! response
    stop
  }

  def stop: Unit = {
    timeoutMessenger.cancel()
    context.stop(self)
  }

  import context.dispatcher
  val timeoutMessenger = context.system.scheduler.scheduleOnce(timeout) {
    onTimeout
  }

  def onTimeout: Unit = replyAndStop(Failure(new TimeoutException))

  def onError: Receive = LoggingReceive {
    case f: Failure => replyAndStop(f)
    case _ => replyAndStop(Failure(new MessageNotSupported))
  }
}
