package pl.learning.sprayio.cameo

import akka.actor.{ActorLogging, Actor, Props, ActorSystem}
import akka.event.LoggingReceive
import pl.learning.sprayio._

object CameoApp extends App {

  runSystem()

  def runSystem() {
    val system = ActorSystem("CameoSystem")
    val serviceA = system.actorOf(Props[InfiniteLoopServiceA], name = "ServiceA")
    val serviceB = system.actorOf(Props[NoResponseServiceB], name = "ServiceB")
    val serviceC = system.actorOf(Props[ServiceC], name = "ServiceC")
    val serviceABC = system.actorOf(CameoDelegatingActor.props(serviceA, serviceB, serviceC), name = "ServiceABC")

    val listener = system.actorOf(Props(new Actor() with ActorLogging {
      def receive = LoggingReceive {
        case response: ResponseABC =>
          log.debug(s"got $response: ${response.valueA} ${response.valueB} ${response.valueC}")
          context.system.shutdown()
        case WorkTimeout =>
          log.debug("got Timeout")
          context.system.shutdown()
        case GetResponseABC =>
          serviceABC ! GetResponseABC
      }
    }))

    listener ! GetResponseABC
  }
}
