package pl.learning.sprayio.cameo

import akka.actor.Status.Failure
import akka.actor.{ ActorLogging, Props, ActorRef, Actor }
import akka.event.LoggingReceive
import pl.learning.sprayio._
import scala.concurrent.duration._

case object WorkTimeout

object CameoActor {
  def props(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) = Props(new CameoActor(originalSender, serviceA, serviceB, serviceC))
}

class CameoActor(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  var responseFromServiceA = Option.empty[String]
  var responseFromServiceB = Option.empty[String]
  var responseFromServiceC = Option.empty[String]

  def receive = LoggingReceive {
    case GetResponseABC =>
      serviceA ! GetResponse
      serviceB ! GetResponse
      serviceC ! GetResponse
      context.become(waitingForResponses)
  }

  def waitingForResponses: Receive = LoggingReceive {
    case ResponseA(value) =>
      responseFromServiceA = Some(value)
      collectResults
    case ResponseB(value) =>
      responseFromServiceB = Some(value)
      collectResults
    case ResponseC(value) =>
      responseFromServiceC = Some(value)
      collectResults
  }

  def collectResults: Unit = for {
    a <- responseFromServiceA
    b <- responseFromServiceB
    c <- responseFromServiceC
  } yield sendResponseAndShutdown(ResponseABC(a, b, c))

  def sendResponseAndShutdown(response: AnyRef) = {
    originalSender ! response
    context.stop(self)
  }

  import context.dispatcher
  val timeoutMessenger = context.system.scheduler.scheduleOnce(50.millisecond) {
    sendResponseAndShutdown(Failure(TimeoutException()))
  }
  // context.setReceiveTimeout(50 milliseconds) // this line is wrong:  ReceiveTimeout will be never sent if service A is sending ResponseA in loop and service B is not sending response at all
}