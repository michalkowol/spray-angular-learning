package pl.learning.sprayio

import pl.learning.sprayio.cameo.CameoRoute
import pl.learning.sprayio.dwarf.DwarfRoute
import pl.learning.sprayio.tutorial.{ Calculate, PiApproximation, Master }
import pl.learning.sprayio.zero.{ZeroInNumberResponse, ZeroInNumberRequest, ZeroCounter}
import spray.routing.SimpleRoutingApp
import akka.actor.{ Props, Actor, ActorSystem }
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import spray.http.MediaTypes
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

object Aquarium extends App with SimpleRoutingApp with JsonDirectives {

  implicit val actorSystem = ActorSystem("aquariumSystem")

  implicit val timeout = Timeout(3 seconds)

  lazy val fishRoute = {
    var fishes = Fish.someFish
    get {
      path("hello") {
        complete {
          "Welcome to aquarium"
        }
      } ~ path("twirl") {
        complete {
          pl.agh.txt.test(customer = "michas", orders = List("1", "2", "33"), third = "third").toString
        }
      } ~
        pathPrefix("fish") {
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

  case object GetPiApproximation

  class PiActor extends Actor {
    override def receive = {
      case GetPiApproximation => {
        val master = context.actorOf(Master.props(nrOfWorkers = 4, nrOfElements = 1000, nrOfMessages = 10000, listener = sender))
        master ! Calculate
      }
    }
  }

  lazy val piActor = actorSystem.actorOf(Props[PiActor], name = "piActor")

  lazy val piRoute = {
    get {
      path("pi") {
        complete {
          val pi = (piActor ? GetPiApproximation).mapTo[PiApproximation]
          pi.map(pi => s"Pi: ${pi.pi}")
        }
      }
    }
  }

  lazy val zeroActor = actorSystem.actorOf(Props[ZeroActor], name = "zeroActor")

  class ZeroActor extends Actor {
    override def receive = {
      case zeroInNumberRequest: ZeroInNumberRequest =>
        val zeroCounter = context.actorOf(ZeroCounter.props(4, sender))
        zeroCounter ! zeroInNumberRequest
    }
  }

  lazy val zeroRoute = {
    get {
      path("zero") {
        parameters("number".as[Int]) { number =>
          complete {
            val zeroCount = (zeroActor ? ZeroInNumberRequest(number)).mapTo[ZeroInNumberResponse]
            zeroCount.map(zeroCount => zeroCount.result.toString)
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
    } ~ path("api" / "products") {
      getFromResource("js/store-products.json")
    } ~ getFromResourceDirectory("")
  }

  val dwarfRoute = new DwarfRoute().route
  val cameoRoute = new CameoRoute().route

  val server = startServer(interface = "0.0.0.0", port = 8080) {
    fishRoute ~ waterRoute ~ piRoute ~ zeroRoute ~ dwarfRoute ~ cameoRoute ~ staticResources
  }
}
