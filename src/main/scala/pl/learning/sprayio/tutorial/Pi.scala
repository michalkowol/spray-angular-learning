package pl.learning.sprayio.tutorial

import akka.actor._
import akka.routing.RoundRobinPool

sealed trait PiMessage
case object Calculate extends PiMessage
case class Work(start: Int, nrOfElements: Int) extends PiMessage
case class Result(value: Double) extends PiMessage
case class PiApproximation(pi: Double, duration: Long)

class Worker extends Actor {

  def calculatePiFor(start: Int, nrOfElements: Int): Double = {
    var acc = 0.0
    for (i ← start until (start + nrOfElements))
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
    acc
  }

  def receive = {
    case Work(start, nrOfElements) => {
      sender ! Result(calculatePiFor(start, nrOfElements))
    }
  }
}

object Master {
  def props(nrOfWorkers: Int, nrOfMessages: Int, nrOfElements: Int, listener: ActorRef): Props = {
    Props(new Master(nrOfWorkers, nrOfMessages, nrOfElements, listener))
  }
}

class Master(nrOfWorkers: Int, nrOfMessages: Int, nrOfElements: Int, listener: ActorRef) extends Actor {

  case class State(pi: Double = 0.0, nrOfResults: Int = 0, start: Long = System.currentTimeMillis()) {
    def update(pi: Double = pi, nrOfResults: Int = nrOfResults): State = {
      State(pi, nrOfResults, start)
    }
  }

  val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinPool(nrOfWorkers)), name = "workerRouter")

  var state = State()

  def receive = {
    case Calculate => {
      for (i <- 0 until nrOfMessages) {
        workerRouter ! Work(i * nrOfElements, nrOfElements)
      }
    }
    case Result(value) => {
      state = state.update(pi = state.pi + value, nrOfResults = state.nrOfResults + 1)
      if (state.nrOfResults == nrOfMessages) {
        listener ! PiApproximation(state.pi, duration = System.currentTimeMillis - state.start)
        context.stop(self)
      }
    }
  }
}

class Listener extends Actor {
  def receive = {
    case PiApproximation(pi, duration) =>
      println(s"\n\tPi approximation: ${pi}\n\tCalculation time: ${duration}")
      context.system.shutdown()
  }
}

object Pi /*extends App*/ {

  calculate(nrOfWorkers = 4, nrOfElements = 1000, nrOfMessages = 10000)

  def calculate(nrOfWorkers: Int, nrOfElements: Int, nrOfMessages: Int) {
    val system = ActorSystem("PiSystem")
    val listener = system.actorOf(Props[Listener], name = "listener")
    val master = system.actorOf(Master.props(nrOfWorkers, nrOfMessages, nrOfElements, listener), name = "master")
    master ! Calculate
  }
}