package pl.learning.sprayio.cameo

import akka.actor.{Props, ActorLogging, Actor, ActorRef}
import akka.event.LoggingReceive

object PingActor {
  def props(originalSender: ActorRef, networkActor: ActorRef): Props = Props(new PingActor(originalSender, networkActor))
  case object Ping
  case object Pong
  case object Up
  case object Down
}

class PingActor(val originalSender: ActorRef, networkActor: ActorRef) extends Actor with ActorLogging with Cameo {

  import PingActor._

  networkActor ! Ping

  def receive: Receive = LoggingReceive {
    case Pong => sendResponseAndShutdown(Up)
  } orElse onError

  override def onTimeout: Unit = sendResponseAndShutdown(Down)
}
