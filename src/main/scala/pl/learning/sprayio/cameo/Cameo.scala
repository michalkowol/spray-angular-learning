package pl.learning.sprayio.cameo

import akka.actor.{ActorLogging, Props, ActorRef, Actor}
import akka.event.LoggingReceive
import scala.concurrent.duration._
import scala.language.postfixOps

trait CameoMessages
case class ResponseA(value: String) extends CameoMessages
case class ResponseB(value: String) extends CameoMessages
case class ResponseAB(valueA: String, valueB: String) extends CameoMessages
case object WorkTimeout extends CameoMessages

case object GetResponse
case object GetResponseAB

object CameoActor {
  def props(originalSender: ActorRef) = Props(new CameoActor(originalSender))
}

class CameoActor(originalSender: ActorRef) extends Actor with ActorLogging {

  var responseFromServiceA: Option[String] = None
  var responseFromServiceB: Option[String] = None

  def receive = LoggingReceive {
    case ResponseA(value) => {
      println("got response from A")
      responseFromServiceA = Some(value)
      collectResults()
    }
    case ResponseB(value) => {
      println("got response from B")
      responseFromServiceB = Some(value)
      collectResults()
    }
    case WorkTimeout => {
      println("sending Timeout")
      sendResponseAndShutdown(WorkTimeout)
    }
  }

  def collectResults() = (responseFromServiceA, responseFromServiceB) match {
    case (Some(a), Some(b)) =>
      timeoutMessenger.cancel()
      sendResponseAndShutdown(ResponseAB(a, b))
    case _ =>
  }

  def sendResponseAndShutdown(response: Any) = {
    println(s"sending response: $response")
    originalSender ! response
    context.stop(self)
  }

  import context.dispatcher // ???
  val timeoutMessenger = context.system.scheduler.scheduleOnce(250 millisecond) {
    self ! WorkTimeout
  }
}

object DelegatingActor {
  def props(someServiceA: ActorRef, someServiceB: ActorRef) = Props(new DelegatingActor(someServiceA, someServiceB))
}

class DelegatingActor(someServiceA: ActorRef, someServiceB: ActorRef) extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponseAB => {
      println("getting response from A and B")
      val originalSender = sender
      val cameoWorker = context.actorOf(CameoActor.props(originalSender))
      someServiceA.tell(GetResponse, cameoWorker)
      someServiceB.tell(GetResponse, cameoWorker)
    }
  }
}