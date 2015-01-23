package pl.learning.sprayio

import spray.routing.{ Directives, Route }
import spray.http.MediaTypes

trait CustomDirectives extends Directives {

  def getJson(route: Route): Route = get {
    respondWithMediaType(MediaTypes.`application/json`) { route }
  }
}
