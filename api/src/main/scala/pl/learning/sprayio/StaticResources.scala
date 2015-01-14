package pl.learning.sprayio

import pl.learning.sprayio.Aquarium._
import spray.routing.Directives

trait StaticResources extends Directives {

  lazy val staticResources = {
    pathEndOrSingleSlash {
      getFromResource("index.html")
    } ~ path("api" / "products") {
      getFromResource("data/store-products.json")
    } ~ getFromResourceDirectory("")
  }
}
