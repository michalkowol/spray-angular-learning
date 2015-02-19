package pl.learning.sprayio

import spray.http.MediaTypes
import spray.routing.{Directives, Route}

trait CustomDirectives extends Directives {

  def getJson(route: Route): Route = get {
    respondWithMediaType(MediaTypes.`application/json`) { route }
  }
}
