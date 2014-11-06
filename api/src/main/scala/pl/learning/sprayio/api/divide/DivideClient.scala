package pl.learning.sprayio.api.divide

import akka.actor.Actor
import pl.learning.sprayio.api.NotifyOnError

class DivideClient extends Actor {

  override def receive = {
    case DivideNumbers(a, b) => NotifyOnError {
      sender ! DivideResult(a / b)
    }
  }
}
