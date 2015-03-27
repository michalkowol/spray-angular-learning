package pl.learning.sprayio.api.db.slick

import akka.actor._
import akka.event.LoggingReceive
import com.paypal.cascade.akka.actor.ServiceActor
import com.paypal.cascade.common.option._
import com.typesafe.scalalogging.slf4j.StrictLogging
import pl.learning.db
import pl.learning.db.slick.dao.{AddressesDAO, PeopleAddressesDAO, PeopleDAO}
import pl.learning.sprayio.api.NotFound
import pl.learning.sprayio.api.pattern.{ActorRefMaker, CameoActor, PerRequestCreator}
import pl.learning.sprayio.marshallers._
import spray.routing.{HttpService, Route}

import scala.concurrent.Future
import scala.concurrent.duration._

object DBService {
  case class Address(city: String, street: String)
  case class Person(name: String, age: Int, addresses: Seq[Address])
}

trait DBService extends HttpService with StrictLogging with PerRequestCreator {

  import DBService._

  def slick: Route =
    pathPrefix("db" / "slick" / "people") {
      get {
        path(IntNumber) { id =>
          getPersonWithAddress {
            ExternalAccess.GetPersonWithAddressById(id)
          }
        }
      } ~ post {
        entity(as[Person]) { person =>
          createPersonWithAddress {
            ExternalAccess.CreatePersonWithAddress(person)
          }
        }
      } ~ get{
        pathEndOrSingleSlash {
          getPeople {
            ExternalAccess.GetPeople
          }
        }
      }
    }

  private def getPersonWithAddress(message: Any): Route = { ctx =>
    perRequest(ctx, PersonMapperRefMaker, message, 3.seconds)
  }

  private def createPersonWithAddress(message: Any): Route = { ctx =>
    perRequest(ctx, PersonUnmapperRefMaker, message, 10.seconds)
  }

  private def getPeople(message: Any): Route = { ctx =>
    perRequest(ctx, AllPeopleGetterRefMaker, message, 10.seconds)
  }
}

object AllPeopleGetterRefMaker extends ActorRefMaker {
  override def create(context: ActorRefFactory, originalSender: ActorRef): ActorRef =
    context.actorOf(Props(new AllPeopleGetter(originalSender)), "AllPeopleGetter")
}
class AllPeopleGetter(val originalSender: ActorRef) extends CameoActor {
  private val externalAccess = context.actorOf(Props[ExternalAccess], "ExternalAccess")

  override def receive: Actor.Receive = LoggingReceive {
    case ExternalAccess.GetPeople =>
      externalAccess ! ExternalAccess.GetPeople
    case ExternalAccess.People(people) =>
      replyAndStop(people)
  }
}

object PersonUnmapperRefMaker extends ActorRefMaker {
  override def create(context: ActorRefFactory, originalSender: ActorRef): ActorRef =
    context.actorOf(Props(new PersonUnmapper(originalSender)), "PersonUnmapper")
}
class PersonUnmapper(val originalSender: ActorRef) extends CameoActor {

  import pl.learning.db._

  private val externalAccess = context.actorOf(Props[ExternalAccess], "ExternalAccess")
  private var addresses = Option.empty[Seq[(db.Address, City)]]
  private var input = Option.empty[DBService.Person]
  private var person = Option.empty[Person]

  override def receive: Actor.Receive = LoggingReceive {
    case ExternalAccess.CreatePersonWithAddress(person) =>
      this.input = person.some
      val addresses = person.addresses
      externalAccess ! ExternalAccess.GetManyAddressByStreetNameAndCity(addresses)
    case ExternalAccess.ManyAddressByStreetNameAndCity(addresses) =>
      this.addresses = addresses.some
      input.map { person => externalAccess ! ExternalAccess.CreatePerson(person.name, person.age) }
    case ExternalAccess.CreatedPerson(person) =>
      this.person = person.some
      addresses.map { addressesWithCities =>
        val addresses = addressesWithCities.map { case (address, _) => address }
        externalAccess ! ExternalAccess.CreatePeronAddressRelation(person, addresses)
      }
    case ExternalAccess.CreatedPeronAddressRelation(_) =>
      person.map(replyAndStop)
  }
}

object PersonMapperRefMaker extends ActorRefMaker {
  override def create(context: ActorRefFactory, originalSender: ActorRef): ActorRef =
    context.actorOf(Props(new PersonMapper(originalSender)), "PersonMapper")
}
class PersonMapper(val originalSender: ActorRef) extends CameoActor {

  private val externalAccess = context.actorOf(Props[ExternalAccess], "ExternalAccess")

  override def receive: Receive = LoggingReceive {
    case q: ExternalAccess.GetPersonWithAddressById =>
      externalAccess ! q
    case ExternalAccess.PersonWithAddressById(Some((person, fullAddresses))) =>
      val addresses = fullAddresses.map { case (a, c) => DBService.Address(a.street, c.name) }
      replyAndStop(DBService.Person(person.name, person.age, addresses))
    case ExternalAccess.PersonWithAddressById(None) =>
      replyAndStop(NotFound(s"Person with full address not found"))
  }
}

object ExternalAccess {

  import pl.learning.db._

  case object GetPeople
  case class People(people: Seq[Person])
  case class GetPersonWithAddressById(id: Int)
  case class PersonWithAddressById(result: Option[(Person, Seq[(db.Address, City)])])
  case class CreatePersonWithAddress(person: DBService.Person)
  case class GetManyAddressByStreetNameAndCity(addresses: Seq[DBService.Address])
  case class ManyAddressByStreetNameAndCity(result: Seq[(db.Address, City)])
  case class CreatePerson(name: String, age: Int)
  case class CreatedPerson(person: Person)
  case class CreatePeronAddressRelation(person: Person, addresses: Seq[db.Address])
  case class CreatedPeronAddressRelation(ids: Seq[Int])
}
class ExternalAccess extends ServiceActor {

  import akka.pattern.pipe
  import context.dispatcher
  import pl.learning.sprayio.api.db.slick.ExternalAccess._

  override def receive: Receive = LoggingReceive {
    case GetPersonWithAddressById(id) =>
      val personWithAddressById = PeopleDAO.getPersonAndAddressById(id).map(PersonWithAddressById)
      personWithAddressById pipeTo sender()
    case GetManyAddressByStreetNameAndCity(addresses) =>
      val addressesFutures = addresses.map { address =>
        AddressesDAO.getOrCreate(address.street, address.city)
      }
      val manyAddressByStreetNameAndCity = Future.sequence(addressesFutures).map(ManyAddressByStreetNameAndCity)
      manyAddressByStreetNameAndCity pipeTo sender()
    case CreatePerson(name, age) =>
      val result = PeopleDAO.createPerson(name, age).map(CreatedPerson)
      result pipeTo sender()
    case CreatePeronAddressRelation(person, addresses) =>
      val relationsFutures = addresses.map { address =>
        PeopleAddressesDAO.createRelation(person, address)
      }
      val result = Future.sequence(relationsFutures).map(CreatedPeronAddressRelation)
      result pipeTo sender()
    case GetPeople =>
      val people = PeopleDAO.list.map(People)
      people pipeTo sender()
  }
}
