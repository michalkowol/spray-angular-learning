package pl.learning.sprayio.cameo

import akka.actor.Status.Failure
import akka.actor._
import akka.event.LoggingReceive
import pl.learning.sprayio._
import scala.concurrent.duration._

import pl.learning.sprayio.api.gathering._

case object WorkTimeout

object CameoActorPlain {
  def props(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef): Props =
    Props(new CameoActorPlain(originalSender, serviceA, serviceB, serviceC))
}

class CameoActorPlain(originalSender: ActorRef, serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  private var responseFromServiceA = Option.empty[String]
  private var responseFromServiceB = Option.empty[String]
  private var responseFromServiceC = Option.empty[String]

  def receive: Receive = LoggingReceive {
    case GetResponseABC =>
      serviceA ! GetResponse
      serviceB ! GetResponse
      serviceC ! GetResponse
      context.become(waitingForResponses)
  }

  private def waitingForResponses: Receive = LoggingReceive {
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

  private def collectResults: Unit = for {
    a <- responseFromServiceA
    b <- responseFromServiceB
    c <- responseFromServiceC
  } yield sendResponseAndShutdown(ResponseABC(a, b, c))

  private def sendResponseAndShutdown(response: AnyRef) = {
    timeoutMessenger.cancel()
    originalSender ! response
    context.stop(self)
  }

  import context.dispatcher
  private val timeoutMessenger: Cancellable = context.system.scheduler.scheduleOnce(50.millisecond) {
    sendResponseAndShutdown(Failure(new TimeoutException))
  }
  // context.setReceiveTimeout(50 milliseconds) // this line is wrong:  ReceiveTimeout will be never sent
  // if service A is sending ResponseA in loop and service B is not sending response at all
}
