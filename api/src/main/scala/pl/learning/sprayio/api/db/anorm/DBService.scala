package pl.learning.sprayio.api.db.anorm

import pl.learning.db.Person
import pl.learning.db.anorm.dao.PeopleDAO
import pl.learning.sprayio.api.pattern.PerRequestCreator
import spray.routing.{HttpService, Route}
import pl.learning.sprayio.marshallers._

import scala.concurrent.ExecutionContext.Implicits.global

trait DBService extends HttpService with PerRequestCreator {

  def anrom: Route =
    pathPrefix("db" / "anorm" / "people") {
      pathEndOrSingleSlash {
        get {
          list
        } ~ post {
          entity(as[Person]) {
            create
          }
        }
      } ~ get {
        path(IntNumber) {
          getById
        }
      }
    }

  private def list: Route = complete {
    PeopleDAO.list
  }

  private def getById(id: Int): Route = complete {
    PeopleDAO.getById(id)
  }

  private def create(person: Person): Route = complete {
    PeopleDAO.create(person)
  }
}
