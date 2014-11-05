package pl.learning.sprayio

import akka.actor.ActorLogging
import pl.learning.sprayio.api.gathering.GatheringHttpService
import pl.learning.sprayio.api.health.HealthHttpService
import spray.routing.HttpServiceActor

class Api extends HttpServiceActor with ActorLogging with HealthHttpService with GatheringHttpService {

  def receive = runRoute {
    healthRoute ~
    gatheringRoute ~
    pathEndOrSingleSlash {
      getFromResource("index.html")
    }
  }
}

