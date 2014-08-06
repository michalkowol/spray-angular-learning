package pl.learning.sprayio.zero

import akka.actor.{ ActorRef, Props, Actor }
import akka.routing.RoundRobinPool

sealed trait ZeroMessages
case class ZeroInTextRequest(text: String) extends ZeroMessages
case class ZeroInTextResponse(zeroCount: Int) extends ZeroMessages
case class ZeroInNumberRequest(number: Int) extends ZeroMessages
case class ZeroInNumberResponse(result: Int) extends ZeroMessages

class ZeroCounterInText extends Actor {

  def receive = {
    case ZeroInTextRequest(text) =>
      val characters = text.toCharArray
      val zeroCount = characters.foldLeft(0) { (acc, character) => if (character == '0') acc + 1 else acc }
      sender ! ZeroInTextResponse(zeroCount)
  }
}

object ZeroCounter {
  def props(nrOfWorkers: Int = 4, originalSender: ActorRef): Props = Props(new ZeroCounter(nrOfWorkers, originalSender))
}

class ZeroCounter(nrOfWorkers: Int = 4, originalSender: ActorRef) extends Actor {

  val zeroCounterInTextRouter = context.actorOf(Props[ZeroCounterInText].withRouter(RoundRobinPool(nrOfWorkers)))

  var number = 0
  var responses = 0
  var zeroCountTotal = 0

  def receive = {
    case ZeroInNumberRequest(number) =>
      this.number = number
      for (i <- 0 to number) {
        zeroCounterInTextRouter ! ZeroInTextRequest(i.toString)
      }
    case ZeroInTextResponse(zeroCount) =>
      responses += 1
      zeroCountTotal += zeroCount
      if (responses == number) {
        originalSender ! ZeroInNumberResponse(zeroCountTotal)
        context.stop(self)
      }
  }
}
