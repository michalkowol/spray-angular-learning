package pl.learning.sprayio.perrequest2

import akka.actor.{Props, Actor}
import pl.learning.sprayio._
import spray.routing.{Route, HttpService}

class PerRequestRoute2 extends HttpService with Actor with PerRequestCreator {

  implicit val actorRefFactory = context

  def receive = runRoute(route)

  val serviceA = context.actorOf(Props[ServiceA])
  val serviceB = context.actorOf(Props[ServiceB])
  val serviceC = context.actorOf(Props[ServiceC])

  val route = {
    get {
      path("perrequest2") {
        getResponseABC {
          GetResponseABC
        }
      }
    }
  }

  def getResponseABC(message: RestMessage): Route = {
//    ctx => perRequest(ctx, GatheringActor2.props(serviceA, serviceB, serviceC), message)
    ctx => perRequest(ctx, GatheringActorFactory(serviceA, serviceB, serviceC), message)
  }
}