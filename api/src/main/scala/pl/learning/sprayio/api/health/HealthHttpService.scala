package pl.learning.sprayio.api.health

import akka.actor.Props
import pl.learning.sprayio.api.pattern.PerRequestCreator
import spray.routing.{HttpService, Route}

import scala.concurrent.duration._

trait HealthHttpService extends HttpService with PerRequestCreator {

  private val healthActor = actorRefFactory.actorOf(Props[HealthActor], "HealthActor")

  def healthRoute: Route = get {
    path("health") {
      getHealth {
        HealthActor.GetHealth
      }
    }
  }

  private def getHealth(message: Any): Route = { ctx =>
    perRequest(ctx, healthActor, message, 100.milliseconds)
  }
}
