package pl.learning.sprayio.api.divide

import akka.actor.Props
import pl.learning.sprayio.api.{RestMessage, PerRequestCreator}
import spray.routing.{Route, HttpService}

trait DivideHttpService extends HttpService with PerRequestCreator {

  private val divideClient = actorRefFactory.actorOf(Props[DivideClient], "DivideClient")

  def divideRoute = get {
    path("divide" / IntNumber / IntNumber) { (a, b) =>
      divideNumbers {
        DivideNumbers(a, b)
      }
    }
  }

  private def divideNumbers(message: RestMessage): Route = { ctx =>
    perRequest(ctx, divideClient, message)
  }
}
