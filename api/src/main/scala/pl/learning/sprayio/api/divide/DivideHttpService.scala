package pl.learning.sprayio.api.divide

import akka.actor.Props
import pl.learning.sprayio.api.pattern.PerRequestCreator
import spray.routing.{Route, HttpService}

trait DivideHttpService extends HttpService with PerRequestCreator {

  private val divideClient = actorRefFactory.actorOf(Props[DivideActor], "DivideActor")

  def divideRoute: Route = get {
    path("divide" / IntNumber / IntNumber) { (a, b) =>
      divideNumbers {
        DivideActor.DivideNumbers(a, b)
      }
    }
  }

  private def divideNumbers(message: Any): Route = { ctx =>
    perRequest(ctx, divideClient, message)
  }
}
