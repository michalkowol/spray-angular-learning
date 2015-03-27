package pl.learning.db.anorm.dao

import anorm._
import anorm.SqlParser._
import pl.learning.db.Person
import pl.learning.db.anorm.DB
import pl.learning.sprayio.api.contentnegotiation.ContentNegotiation.{Address => CNAddress, Person => CNPerson}

import scala.concurrent.{ExecutionContext, Future}

object PeopleDAO {

  private val simpleParser = People.id ~ People.name ~ Addresses.street.? map flatten

  private val peopleWithAddressParser = (People.id ~ People.name ~ People.age ~ Addresses.street.? ~ str("city_name").?).map {
    case id ~ name ~ age ~ Some(street) ~ Some(city) => (id, CNPerson(name, age, Nil), Some(CNAddress(street, city)))
    case id ~ name ~ age ~ _ ~ _ => (id, CNPerson(name, age, Nil), None)
  }

  private val peopleWithAddressParserForComprehension = for {
    id <- People.id
    name <- People.name
    age <- People.age
    streetOpt <- Addresses.street.?
    cityOpt <- str("city_name").?
  } yield {
    val person = CNPerson(name, age, Nil)
    (streetOpt, cityOpt) match {
      case (Some(street), Some(city)) => (id, person, Some(CNAddress(street, city)))
      case _ => (id, person, None)
    }
  }

  def peopleWithAddresses: Iterable[CNPerson] = DB.withConnection { implicit c =>
    val sql =
      SQL"""
         SELECT p.id, p.name, p.age, a.street, c.name AS city_name FROM people AS p
         LEFT JOIN addresses_people AS ap ON ap.person_id = p.id
         LEFT JOIN addresses AS a ON a.id = ap.address_id
         LEFT JOIN cities AS c ON c.id = a.city_id
      """
    val peopleWithAddresses = sql.as(peopleWithAddressParser.*).groupBy(_._1).values.map { personWithAddress =>
      val addresses = personWithAddress.map(_._3).flatMap {
        case address@Some(_) => address
        case _ => None
      }
      val person = personWithAddress.head._2
      CNPerson(person.name, person.age, addresses)
    }

    peopleWithAddresses
  }

  def list(implicit ec: ExecutionContext): Future[Iterable[Person]] = DB.withAsyncConnection { implicit c =>
    val sql = SQL"SELECT * FROM people"
    sql.as(People.person.*)
  }

  def getById(id: Long)(implicit ec: ExecutionContext): Future[Option[Person]] = DB.withAsyncConnection { implicit c =>
    val sql = SQL"SELECT * FROM people WHERE id = $id"
    sql.as(People.person.singleOpt)
  }

  def create(person: Person)(implicit ec: ExecutionContext): Future[Option[Person]] = {
    val insert: Future[Option[Long]] = DB.withAsyncConnection { implicit c =>
      val sql = SQL"INSERT INTO people(name, age) VALUES (${person.name}, ${person.age})"
      sql.executeInsert()
    }
    insert.flatMap {
      case Some(id) => getById(id)
      case None => Future.failed(new Throwable("SQL insert error"))
    }
  }
}
