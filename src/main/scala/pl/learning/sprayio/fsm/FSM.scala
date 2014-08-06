package pl.learning.sprayio.fsm

import akka.actor.{ Props, ActorLogging, ActorRef, Actor }
import akka.event.LoggingReceive
import pl.learning.sprayio._

object FSMCollectingPattern {
  def props(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) = Props(new FSMCollectingPattern(serviceA, serviceB, serviceC))
}

class FSMCollectingPattern(serviceA: ActorRef, serviceB: ActorRef, serviceC: ActorRef) extends Actor with ActorLogging {

  def receive = empty

  def empty = LoggingReceive {
    case GetResponseABC =>
      val originalSender = sender
      serviceA ! GetResponse
      serviceB ! GetResponse
      serviceC ! GetResponse
      context become noResponses(originalSender)
  }

  def noResponses(originalSender: ActorRef): Receive = {
    case ResponseA(valueA) => context become receivedA(originalSender, valueA)
    case ResponseB(valueB) => context become receivedB(originalSender, valueB)
    case ResponseC(valueC) => context become receivedC(originalSender, valueC)
  }

  def receivedA(originalSender: ActorRef, valueA: String): Receive = {
    case ResponseA(newValueA) => context become receivedA(originalSender, newValueA)
    case ResponseB(valueB) => context become receivedAB(originalSender, valueA, valueB)
    case ResponseC(valueC) => context become receivedAC(originalSender, valueA, valueC)
  }

  def receivedB(originalSender: ActorRef, valueB: String): Receive = {
    case ResponseA(valueA) => context become receivedAB(originalSender, valueA, valueB)
    case ResponseB(newValueB) => context become receivedB(originalSender, newValueB)
    case ResponseC(valueC) => context become receivedAC(originalSender, valueB, valueC)
  }

  def receivedC(originalSender: ActorRef, valueC: String): Receive = {
    case ResponseA(valueA) => context become receivedAC(originalSender, valueA, valueC)
    case ResponseB(valueB) => context become receivedBC(originalSender, valueB, valueC)
    case ResponseC(newValueC) => context become receivedC(originalSender, newValueC)
  }

  def receivedAB(originalSender: ActorRef, valueA: String, valueB: String): Receive = {
    case ResponseA(newValueA) => context become receivedAB(originalSender, newValueA, valueB)
    case ResponseB(newValueB) => context become receivedAB(originalSender, valueA, newValueB)
    case ResponseC(valueC) => receivedABC(originalSender, valueA, valueB, valueC)
  }

  def receivedAC(originalSender: ActorRef, valueA: String, valueC: String): Receive = {
    case ResponseA(newValueA) => context become receivedAC(originalSender, newValueA, valueC)
    case ResponseB(valueB) => receivedABC(originalSender, valueA, valueB, valueC)
    case ResponseC(newValueC) => context become receivedAC(originalSender, valueA, newValueC)
  }

  def receivedBC(originalSender: ActorRef, valueB: String, valueC: String): Receive = {
    case ResponseA(valueA) => receivedABC(originalSender, valueA, valueB, valueC)
    case ResponseB(newValueB) => context become receivedAC(originalSender, newValueB, valueC)
    case ResponseC(newValueC) => context become receivedAC(originalSender, valueB, newValueC)
  }

  def receivedABC(originalSender: ActorRef, valueA: String, valueB: String, valueC: String): Unit = {
    originalSender ! ResponseABC(valueA, valueB, valueC)
    context become empty
  }
}
