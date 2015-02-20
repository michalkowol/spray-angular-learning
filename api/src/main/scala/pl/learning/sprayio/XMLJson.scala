package pl.learning.sprayio

import spray.routing.{Route, HttpService}
import pl.learning.sprayio.marshallers._

case class Address(city: String, street: String)
case class Person(name: String, age: Int, address: Seq[Address])

trait XmlJson extends HttpService {

  def xmlJson: Route = get {
    path("xmlOrJson") {
      pathEnd {
        complete {
          Person("michal", 25, Seq(Address("Gliwice", "Chemiczna"), Address("Warszawa", "Pulawska")))
        }
      }
    }
  }
}
