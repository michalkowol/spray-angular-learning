package pl.learning.sprayio.api.health

import akka.actor.Props
import pl.learning.sprayio.api.{ RestMessage, PerRequestCreator }
import spray.routing.{ Route, HttpService }

import scala.language.postfixOps
import scala.concurrent.duration._

trait HealthHttpService extends HttpService with PerRequestCreator {

  private val healthClient = actorRefFactory.actorOf(Props[HealthClient])

  def healthRoute = get {
    path("health") {
      getHealth {
        GetHealth
      }
    }
  }

  private def getHealth(message: RestMessage): Route = { ctx =>
    perRequest(ctx, healthClient, message, 100 milliseconds)
  }
}
