package pl.learning.sprayio

import pl.learning.sprayio.api.divide.DivideHttpService
import pl.learning.sprayio.api.gathering.GatheringHttpService
import pl.learning.sprayio.api.health.HealthHttpService
import pl.learning.sprayio.api.sameactorproblem.AggregatorHttpService
import pl.learning.sprayio.cameo.CameoRoute
import spray.routing.HttpServiceActor

class Api
    extends HttpServiceActor
    with HealthHttpService
    with GatheringHttpService
    with StaticResources
    with DivideHttpService
    with AggregatorHttpService
    with XmlJson
    with CameoRoute {

  def receive: Receive = runRoute {
    pathPrefix("api") {
      healthRoute ~
        gatheringRoute ~
        divideRoute ~
        aggregateRoute ~
        xmlJson ~
        cameoRoute
    } ~
      staticResources
  }
}

