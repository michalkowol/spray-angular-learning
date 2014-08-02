package pl.learning.sprayio.cameo

import akka.actor.{ActorLogging, Actor, Props, ActorSystem}
import akka.event.LoggingReceive

object CameoApp extends App {

  runSystem()

  def runSystem() {
    val system = ActorSystem("CameoSystem")
    val serviceA = system.actorOf(Props[ServiceA], name = "ServiceA")
    val serviceB = system.actorOf(Props[ServiceB], name = "ServiceB")
    val serviceAB = system.actorOf(DelegatingActor.props(serviceA, serviceB), name = "ServiceAB")

    val listener = system.actorOf(Props(new Actor() with ActorLogging {
      def receive = LoggingReceive {
        case response: ResponseAB => {
          println(s"got $response")
          context.system.shutdown()
        }
        case WorkTimeout => {
          println("got Timeout")
          context.system.shutdown()
        }
        case GetResponseAB => {
          serviceAB ! GetResponseAB
        }
      }
    }))

    listener ! GetResponseAB
  }
}
