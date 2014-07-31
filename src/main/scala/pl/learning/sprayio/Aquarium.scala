package pl.learning.sprayio

import spray.routing.SimpleRoutingApp
import akka.actor.{ Actor, Props, ActorSystem }
import spray.http.MediaTypes
import scala.language.postfixOps

object Aquarium extends App with SimpleRoutingApp with JsonDirectives {

  implicit val system = ActorSystem("aquariumSystem")

  lazy val fishRoute = {
    var fishes = Fish.someFish
    get {
      path("hello") {
        complete {
          "Welcome to aquarium"
        }
      } ~ pathPrefix("fish") {
        path("all") {
          respondWithMediaType(MediaTypes.`application/json`) {
            complete {
              Fish.toJson(fishes)
            }
          }
        } ~ path("all" / "pacific") {
          respondWithMediaType(MediaTypes.`application/json`) {
            complete {
              Fish.toJson(Tuna("pacific", 50) +: fishes)
            }
          }
        }
      }
    } ~ getJson {
      path("all" / "withJson") {
        complete {
          Fish.toJson(fishes)
        }
      }
    } ~ post {
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

  class SprayListener extends Actor {
    import pl.learning.sprayio.tutorial._

    def receive = {
      case PiApproximation(pi, duration) =>
        complete {
          s"\n\tPi approximation: ${pi}\n\tCalculation time: ${duration}"
        }
    }
  }

  lazy val waterRoute = {
    get {
      path("waterlevel") {
        complete {
          import pl.learning.sprayio.tutorial._
          val listener = system.actorOf(Props[Listener])
          val master = system.actorOf(Props(new Master(nrOfWorkers = 4, nrOfElements = 1000, nrOfMessages = 10000, listener = listener)))
          master ! Calculate
          "aaa"
        }
      }
    }
  }

  lazy val staticResources = {
    path("") {
      getFromResource("index.html")
    } ~ path("api" / "products") {
      getFromResource("js/store-products.json")
    } ~ getFromResourceDirectory("")
  }

  val server = startServer(interface = "0.0.0.0", port = 8080) {
    fishRoute ~ waterRoute ~ staticResources
  }
}
