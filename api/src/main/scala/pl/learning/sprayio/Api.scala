package pl.learning.sprayio

import akka.actor.ActorLogging
import pl.learning.sprayio.api.gathering.GatheringHttpService
import pl.learning.sprayio.api.health.HealthHttpService
import spray.routing.HttpServiceActor

class Api extends HttpServiceActor with ActorLogging with HealthHttpService with GatheringHttpService with StaticResources {

  def receive = runRoute {
    pathPrefix("api") {
      healthRoute ~
      gatheringRoute
    } ~
    staticResources
  }
}

