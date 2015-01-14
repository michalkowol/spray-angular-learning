package pl.learning.sprayio.api.health

import akka.actor.Props
import pl.learning.sprayio.api.PerRequestCreator
import spray.routing.{ Route, HttpService }

import scala.concurrent.duration._

trait HealthHttpService extends HttpService with PerRequestCreator {

  private val healthActor = actorRefFactory.actorOf(Props[HealthActor], "HealthActor")

  def healthRoute = get {
    path("health") {
      getHealth {
        HealthActor.GetHealth
      }
    }
  }

  private def getHealth(message: Any): Route = { ctx =>
    perRequestWithRef(ctx, healthActor, message, 100.milliseconds)
  }
}
