package pl.learning.sprayio.api.divide

import akka.actor.Actor
import akka.event.LoggingReceive
import pl.learning.sprayio.api.pattern.NotifyOnError

object DivideActor {
  case class DivideNumbers(a: Int, b: Int)
  case class DivideResult(result: Int, successCount: Int)
}

class DivideActor extends Actor {
  import DivideActor._

  var successCount = 0

  override def receive: Receive = LoggingReceive {
    case DivideNumbers(a, b) => NotifyOnError {
      successCount += 1
      sender() ! DivideResult(a / b, successCount)
    }
  }
}
