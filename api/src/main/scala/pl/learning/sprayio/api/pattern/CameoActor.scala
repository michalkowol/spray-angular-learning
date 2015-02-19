package pl.learning.sprayio.api.pattern

import akka.actor.Status.Failure
import akka.actor.{ActorRef, UnhandledMessage}
import com.paypal.cascade.akka.actor.{CommonActor, UnhandledMessageException}
import com.paypal.cascade.akka.mailbox.ExpiredLetter
import pl.learning.sprayio.TimeoutException

import scala.concurrent.duration._

trait CameoActor extends CommonActor {

  def originalSender: ActorRef
  def timeout: FiniteDuration = 250.milliseconds

  def replyAndStop(response: Any): Unit = {
    originalSender ! response
    stop()
  }

  def stop(): Unit = {
    context.stop(self)
  }

  import context.dispatcher
  val timeoutMessenger = context.system.scheduler.scheduleOnce(timeout) {
    onTimeout()
  }
  def onTimeout(): Unit = replyAndStop(Failure(new TimeoutException))

  override def postStop(): Unit = {
    timeoutMessenger.cancel()
  }

  @throws[UnhandledMessageException]
  override def unhandled(message: Any): Unit = {
    message match {
      case em: ExpiredLetter => context.system.eventStream.publish(UnhandledMessage(message, sender(), self))
      case failure@Failure(t: Throwable) =>
        super.unhandled(message)
        replyAndStop(failure)
        log.error(s"Unhandled failure message for actor: ${self.path}, sender: ${sender()}, message: ${t.getClass}")
      case _ =>
        super.unhandled(message)
        val ex = new UnhandledMessageException(s"Unhandled message received by actor: ${self.path}, sender: ${sender()}, message: ${message.getClass}")
        replyAndStop(Failure(ex))
        throw ex
    }
  }
}
