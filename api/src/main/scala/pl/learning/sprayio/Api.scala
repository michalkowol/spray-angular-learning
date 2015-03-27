package pl.learning.sprayio

import pl.learning.sprayio.api.contentnegotiation.ContentNegotiation
import pl.learning.sprayio.api.db.slick.{DBService => SlickDBService}
import pl.learning.sprayio.api.db.anorm.{DBService => AnormDBService}
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
    with ContentNegotiation
    with SlickDBService
    with AnormDBService
    with CameoRoute {

  def receive: Receive = runRoute {
    pathPrefix("api") {
      healthRoute ~
        gatheringRoute ~
        divideRoute ~
        aggregateRoute ~
        contentNegotiation ~
        slick ~
        anrom ~
        cameoRoute
    } ~
      staticResources
  }
}

