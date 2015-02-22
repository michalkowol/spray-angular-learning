package pl.learning.sprayio.api.contentnegotiation

import pl.learning.sprayio.marshallers._
import spray.routing.{HttpService, Route}

import scala.collection.mutable.{MutableList => MList}

object ContentNegotiation {
  case class Address(city: String, street: String)
  case class Person(name: String, age: Int, addresses: Seq[Address])
}

trait ContentNegotiation extends HttpService {

  import pl.learning.sprayio.api.contentnegotiation.ContentNegotiation._
  private val people = MList(
    Person("michal", 25, Seq(Address("Gliwice", "Chemiczna"), Address("Warszawa", "Pulawska")))
  )

  def contentNegotiation: Route =
    path("contentNegotiation") {
      pathEndOrSingleSlash {
        get {
          complete {
            people
          }
        } ~ post {
          entity(as[Person]) { person =>
            people += person
            complete {
              people
            }
          }
        }
      }
    }
}
