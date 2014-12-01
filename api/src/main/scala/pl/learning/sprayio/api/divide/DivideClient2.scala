package pl.learning.sprayio.api.divide

import akka.actor.Actor
import akka.actor.Status.Failure
import akka.event.LoggingReceive
import pl.learning.sprayio.api.NotifyOnError

class DivideClient2 extends Actor {

  var successCount = 0

  override def receive = LoggingReceive {
    case DivideNumbers(a, b) =>
      successCount += 1
      sender() ! DivideResult(a / b, successCount)
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    sender() ! Failure(reason)
    super.preRestart(reason, message)
  }
}
