package pl.learning.sprayio.api.divide

import akka.actor.Props
import pl.learning.sprayio.api.pattern.PerRequestCreator
import spray.routing.{HttpService, Route}

trait DivideHttpService extends HttpService with PerRequestCreator {

  private val divideActor = actorRefFactory.actorOf(Props[DivideActor], "DivideActor")

  def divideRoute: Route = get {
    path("divide" / IntNumber / IntNumber) { (a, b) =>
      divideNumbers {
        DivideActor.DivideNumbers(a, b)
      }
    }
  }

  private def divideNumbers(message: Any): Route = { ctx =>
    perRequest(ctx, divideActor, message)
  }
}
