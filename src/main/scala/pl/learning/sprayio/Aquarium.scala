package pl.learning.sprayio

import spray.routing.{Route, SimpleRoutingApp}
import akka.actor.ActorSystem
import spray.http.MediaTypes
import scala.language.postfixOps

object Aquarium extends App with SimpleRoutingApp with JsonDirectives {
  implicit val actorSystem = ActorSystem()

  lazy val fishRoute = {
    var fishes = Fish.someFish
    get {
      path("hello") {
        complete {
          "Welcome to aquarium"
        }
      } ~
      pathPrefix("fish") {
        path("all") {
          respondWithMediaType(MediaTypes.`application/json`) {
            complete {
              Fish.toJson(fishes)
            }
          }
        } ~
        path("all" / "pacific") {
          respondWithMediaType(MediaTypes.`application/json`) {
            complete {
              Fish.toJson(Tuna("pacific", 50) +: fishes)
            }
          }
        }
      }
    } ~
    getJson {
      path("all" / "withJson") {
        complete {
          Fish.toJson(fishes)
        }
      }
    } ~
    post {
      path("fish" / "add" / "tuna") {
        parameters("ocean"?, "age".as[Int], "weight" ? 20) { (ocean, age, weight) =>
          val newTuna = Tuna(ocean.getOrElse("pacific"), age)
          fishes = newTuna +: fishes
          complete {
            val size = if (weight < 10) "small" else "big"
            s"OK $size"
          }
        }
      }
    }
  }

  lazy val waterRoute = {
    get {
      path("waterlevel") {
        complete {
          val wl = 10
          s"The water level is $wl"
        }
      }
    }
  }

  lazy val staticResources = {
    path("") {
      getFromResource("index.html")
    } ~
    path("api" / "products") {
      getFromResource("js/store-products.json")
    } ~
    getFromResourceDirectory("")
  }

  startServer(interface = "0.0.0.0", port = 8080) {
    fishRoute ~ waterRoute ~ staticResources
  }
}
