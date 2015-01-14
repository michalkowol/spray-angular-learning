package pl.learning.sprayio.cameo

import akka.actor.Status.Failure
import akka.actor.{ ActorRef, Actor }
import scala.concurrent.duration._

trait Cameo {
  this: Actor =>

  def originalSender: ActorRef
  def timeout: FiniteDuration = 250.milliseconds

  def sendResponseAndShutdown(response: AnyRef) = {
    timeoutMessenger.cancel()
    originalSender ! response
    context.stop(self)
  }

  import context.dispatcher
  val timeoutMessenger = context.system.scheduler.scheduleOnce(timeout) {
    onTimeout
  }

  def onTimeout: Unit = sendResponseAndShutdown(Failure(TimeoutException()))
}
