package pl.learning.sprayio.api.divide

import akka.actor.Props
import pl.learning.sprayio.api.{ RestMessage, PerRequestCreator }
import spray.routing.{ Route, HttpService }

trait DivideHttpService extends HttpService with PerRequestCreator {

  private val divideClient = actorRefFactory.actorOf(Props[DivideActor], "DivideActor")

  def divideRoute = get {
    path("divide" / IntNumber / IntNumber) { (a, b) =>
      divideNumbers {
        DivideActor.DivideNumbers(a, b)
      }
    }
  }

  private def divideNumbers(message: Any): Route = { ctx =>
    perRequestWithRef(ctx, divideClient, message)
  }
}
