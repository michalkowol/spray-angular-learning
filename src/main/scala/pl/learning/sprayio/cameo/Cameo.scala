package pl.learning.sprayio.cameo

import akka.actor.{ActorLogging, Props, ActorRef, Actor}
import akka.event.LoggingReceive
import scala.concurrent.duration._
import scala.language.postfixOps

trait CameoMessages
case class ResponseA(value: String) extends CameoMessages
case class ResponseB(value: String) extends CameoMessages
case class ResponseC(value: String) extends CameoMessages
case class ResponseABC(valueA: String, valueB: String, valueC: String) extends CameoMessages
case object WorkTimeout extends CameoMessages

case object GetResponse
case object GetResponseABC

object CameoActor {
  def props(originalSender: ActorRef) = Props(new CameoActor(originalSender))
}

class CameoActor(originalSender: ActorRef) extends Actor with ActorLogging {

  var responseFromServiceA: Option[String] = None
  var responseFromServiceB: Option[String] = None
  var responseFromServiceC: Option[String] = None

  def receive = LoggingReceive {
    case ResponseA(value) => {
      log.debug("got response from A")
      responseFromServiceA = Some(value)
      collectResults()
    }
    case ResponseB(value) => {
      log.debug("got response from B")
      responseFromServiceB = Some(value)
      collectResults()
    }
    case ResponseC(value) => {
      log.debug("got response from C")
      responseFromServiceC = Some(value)
      collectResults()
    }
    case WorkTimeout => {
      log.debug("sending Timeout")
      sendResponseAndShutdown(WorkTimeout)
    }
  }

  def collectResults() = (responseFromServiceA, responseFromServiceB, responseFromServiceC) match {
    case (Some(a), Some(b), Some(c)) =>
      timeoutMessenger.cancel()
      sendResponseAndShutdown(ResponseABC(a, b, c))
    case _ =>
  }

  def sendResponseAndShutdown(response: Any) = {
    log.debug(s"sending response: $response")
    originalSender ! response
    context.stop(self)
  }

  import context.dispatcher // ???
  val timeoutMessenger = context.system.scheduler.scheduleOnce(250 millisecond) {
    self ! WorkTimeout
  }
}

object DelegatingActor {
  def props(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) = Props(new DelegatingActor(serviceA, serviceB, serviceC))
}

class DelegatingActor(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponseABC => {
      log.debug("getting response from A and B")
      val originalSender = sender
      val cameoWorker = context.actorOf(CameoActor.props(originalSender))
      serviceA.tell(GetResponse, cameoWorker)
      serviceB.tell(GetResponse, cameoWorker)
      serviceC.tell(GetResponse, cameoWorker)
    }
  }
}