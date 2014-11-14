package pl.learning.sprayio.cameo

import akka.actor.{ ActorLogging, Props, ActorRef, Actor }
import akka.event.LoggingReceive
import pl.learning.sprayio._
import scala.concurrent.duration._

case object WorkTimeout

object CameoActor {
  def props(originalSender: ActorRef) = Props(new CameoActor(originalSender))
}

class CameoActor(originalSender: ActorRef) extends Actor with ActorLogging {

  var responseFromServiceA = Option.empty[String]
  var responseFromServiceB = Option.empty[String]
  var responseFromServiceC = Option.empty[String]

  def receive = LoggingReceive {
    case ResponseA(value) =>
      responseFromServiceA = Some(value)
      collectResults()
    case ResponseB(value) =>
      responseFromServiceB = Some(value)
      collectResults()
    case ResponseC(value) =>
      responseFromServiceC = Some(value)
      collectResults()
    case WorkTimeout =>
      sendResponseAndShutdown(WorkTimeout)
  }

  def collectResults() = (responseFromServiceA, responseFromServiceB, responseFromServiceC) match {
    case (Some(a), Some(b), Some(c)) =>
      timeoutMessenger.cancel()
      sendResponseAndShutdown(ResponseABC(a, b, c))
    case _ =>
  }

  def sendResponseAndShutdown(response: Any) = {
    originalSender ! response
    context.stop(self)
  }

  import context.dispatcher
  val timeoutMessenger = context.system.scheduler.scheduleOnce(250.millisecond) {
    self ! WorkTimeout
  }
  // context.setReceiveTimeout(250 milliseconds) // this line is wrong:  ReceiveTimeout will be never sent if service A is sending ResponseA in loop and service B is not sending response at all
}

object CameoDelegatingActor {
  def props(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) = Props(new CameoDelegatingActor(serviceA, serviceB, serviceC))
}

class CameoDelegatingActor(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case GetResponseABC =>
      val originalSender = sender
      val cameoWorker = context.actorOf(CameoActor.props(originalSender))
      serviceA.tell(GetResponse, cameoWorker)
      serviceB.tell(GetResponse, cameoWorker)
      serviceC.tell(GetResponse, cameoWorker)
  }
}