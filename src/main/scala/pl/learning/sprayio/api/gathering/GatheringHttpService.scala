package pl.learning.sprayio.api.gathering

import akka.actor.Props
import pl.learning.sprayio._
import pl.learning.sprayio.api.{RestMessage, PerRequestCreator}
import spray.routing._

import scala.language.postfixOps
import scala.concurrent.duration._

trait GatheringHttpService extends HttpService with PerRequestCreator {

  private val serviceA = actorRefFactory.actorOf(Props[ServiceA], "serviceA")
  private val serviceB = actorRefFactory.actorOf(Props[ServiceB], "serviceB")
  private val serviceC = actorRefFactory.actorOf(Props[ServiceC], "serviceC")

  def gatheringRoute = get {
    path("gathering") {
      getResponseABC {
        GetResponseABC
      }
    }
  }

  private def getResponseABC(message: RestMessage): Route = { ctx =>
    perRequest(ctx, GatheringPropsFactory(serviceA, serviceB, serviceC), message, timeout = 500 milliseconds)
  }
}
