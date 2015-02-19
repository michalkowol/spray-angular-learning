package pl.learning.sprayio

import spray.routing.HttpService

trait StaticResources extends HttpService {

  lazy val staticResources = {
    pathEndOrSingleSlash {
      getFromResource("index.html")
    } ~ path("api" / "products") {
      getFromResource("assets/data/store-products.json")
    } ~ getFromResourceDirectory("assets")
  }
}
