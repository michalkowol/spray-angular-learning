package pl.learning.sprayio.api.gathering

import akka.actor.ActorRef
import akka.event.LoggingReceive
import com.paypal.cascade.akka.actor.ServiceActor
import pl.learning.sprayio.api.pattern.NotifyOnError

import scala.concurrent.duration._
import scala.util.Random

case class ResponseABC(valueA: String, valueB: String, valueC: String)
case object GetResponseABC

case object GetResponse
case class ResponseA(value: String)
case class ResponseB(value: String)
case class ResponseC(value: String)

class ServiceA extends ServiceActor {
  def receive: Receive = LoggingReceive {
    case GetResponse =>
      sender() ! ResponseA("John")
  }
}

class ServiceB extends ServiceActor {
  def receive: Receive = LoggingReceive {
    case GetResponse =>
      sender() ! ResponseB("has")
  }
}

class RandomServiceB extends ServiceActor {

  def receive: Receive = LoggingReceive {
    case GetResponse => NotifyOnError {
      Random.nextInt(3) match {
        case 0 => throw new Exception("Custom exception")
        case 1 => // WARNING: Chrome repeats Request Timeout (408)
        case _ => sender() ! ResponseB("has")
      }
    }
  }
}

class NoResponseServiceB extends ServiceActor {

  def receive: Receive = LoggingReceive {
    case GetResponse => {
    }
  }
}

class InfiniteLoopServiceB extends ServiceActor {

  import context.dispatcher

  case class SendWaitAndRepeat(org: ActorRef)

  def receive: Receive = LoggingReceive {
    case GetResponse => {
      self ! SendWaitAndRepeat(sender())
    }
    case SendWaitAndRepeat(org) => {
      org ! ResponseB("has")
      context.system.scheduler.scheduleOnce(100.millisecond) {
        self ! SendWaitAndRepeat(org)
      }
    }
  }
}

class ServiceC extends ServiceActor {
  def receive: Receive = LoggingReceive {
    case GetResponse =>
      sender() ! ResponseC("cat")
  }
}

object MessageProvider {
  case class Identifiable(id: Any, msg: Any)
}
class MessageProvider extends ServiceActor {
  def receive: Receive = LoggingReceive {
    case number: Int =>
      sender() ! number + 1
    case MessageProvider.Identifiable(id, msg) =>
      import context.dispatcher
      import akka.pattern.{ask, pipe}
      val response = self.ask(msg)(1.second).map(response => MessageProvider.Identifiable(id, response))
      response pipeTo sender()
  }
}
