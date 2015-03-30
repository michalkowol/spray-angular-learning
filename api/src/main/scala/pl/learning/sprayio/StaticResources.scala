package pl.learning.sprayio

import spray.routing.{Route, HttpService}

trait StaticResources extends HttpService {

  def staticResources: Route = {
    pathEndOrSingleSlash {
      getFromResource("assets/index.html")
    } ~ path("api" / "products") {
      getFromResource("assets/data/store-products.json")
    } ~ path("js" / "js-fastopt.js") {
      getFromResource("js-fastopt.js")
    } ~ path("js" / "js-fastopt.js.map") {
      getFromResource("js-fastopt.js.map")
    } ~ path("js" / "js-opt.js") {
      getFromResource("js-opt.js")
    } ~ getFromResourceDirectory("assets")
  }
}
