package pl.learning.sprayio

import akka.actor.ActorLogging
import pl.learning.sprayio.api.divide.DivideHttpService
import pl.learning.sprayio.api.gathering.GatheringHttpService
import pl.learning.sprayio.api.health.HealthHttpService
import pl.learning.sprayio.cameo.CameoRoute
import spray.routing.HttpServiceActor

class Api extends HttpServiceActor with ActorLogging with HealthHttpService with GatheringHttpService with StaticResources with DivideHttpService with CameoRoute {

  def receive = runRoute {
    pathPrefix("api") {
      healthRoute ~
      gatheringRoute ~
      divideRoute ~
      cameoRoute
    } ~
    staticResources
  }
}

