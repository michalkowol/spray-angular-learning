package pl.learning.sprayio.api.divide

import akka.actor.Actor
import akka.actor.Status.Failure
import akka.event.LoggingReceive
import pl.learning.sprayio.api.NotifyOnError

class DivideClient extends Actor {

  var successCount = 0

  override def receive = LoggingReceive {
    case DivideNumbers(a, b) => NotifyOnError {
      successCount += 1
      sender ! DivideResult(a / b, successCount)
    }
  }
}
