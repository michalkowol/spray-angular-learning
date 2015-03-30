package pl.learning.sprayio

import spray.routing.{Route, HttpService}

trait StaticResources extends HttpService {

  def staticResources: Route = {
    pathEndOrSingleSlash {
      getFromResource("assets/index.html")
    } ~ path("api" / "products") {
      getFromResource("assets/data/store-products.json")
    } ~ path("js" / "ui-fastopt.js") {
      getFromResource("ui-fastopt.js")
    } ~ path("js" / "ui-fastopt.js.map") {
      getFromResource("ui-fastopt.js.map")
    } ~ path("js" / "ui-opt.js") {
      getFromResource("ui-opt.js")
    } ~ getFromResourceDirectory("assets")
  }
}
