package pl.learning.sprayio.dwarf

import pl.learning.sprayio.JsonDirectives
import spray.routing._
import akka.actor.{ Actor, Props, ActorSystem }
import akka.pattern.ask
import scala.language.postfixOps
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class DwarfRoute(implicit actorSystem: ActorSystem) extends Directives with JsonDirectives {

  implicit val timeout = Timeout(1 seconds)

  class HelloActor extends Actor {
    override def receive = {
      case ctx: RequestContext => ctx.complete("Welcome to the Land of Dwarfs!")
    }
  }

  class FoodActor extends Actor {
    private val foodSupply = 10

    override def receive = {
      case GetFoodSupply => sender ! foodSupply
    }
  }

  object GetFoodSupply

  lazy val helloActor = actorSystem.actorOf(Props(new HelloActor()))
  lazy val foodActor = actorSystem.actorOf(Props(new FoodActor()))

  var plentyOfDwarfs = Dwarf.someDwarfs
  lazy val dwarfRoute = {
    get {
      path("hello2") { ctx =>
        helloActor ! ctx
      }
    } ~
      getJson {
        path("list" / "all") {
          complete {
            Dwarf.toJson(plentyOfDwarfs)
          }
        }
      } ~
      getJson {
        path("dwarf" / IntNumber / "details") { index =>
          complete {
            Dwarf.toJson(plentyOfDwarfs(index))
          }
        }
      } ~
      post {
        path("dwarf" / "add" / "mining") {
          parameters("mineral"?, "gramsPerHour".as[Int]) { (mineral, gramsPerHour) =>
            val newDwarf = MiningDwarf(mineral.getOrElse("silver"), gramsPerHour)
            plentyOfDwarfs = newDwarf :: plentyOfDwarfs
            complete {
              "OK"
            }
          }
        }
      }
  }

  lazy val supplyRoute = {
    get {
      path("supply" / "food") {
        complete {
          val food = (foodActor ? GetFoodSupply).mapTo[Int]
          food.map(s => s"The supply of the food is $s")
        }
      }
    }
  }

  def path: Route = dwarfRoute ~ supplyRoute
}
