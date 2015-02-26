package pl.learning.db.anorm.dao

import anorm._
import anorm.SqlParser._
import pl.learning.db.anorm.DB
import pl.learning.sprayio.api.contentnegotiation.ContentNegotiation.{Address, Person}

object People {
  val id = int("id")
  val name = str("name")
  val age = int("age")
}

object Addresses {
  val id = int("id")
  val street = str("street")
  val cityId = int("city_id")
}

object Cities {
  val id = int("id")
  val name = str("name")
}

object AddressesPeople {
  val id = int("id")
  val personId = int("person_id")
  val addressId = int("address_id")
}

object PeopleDAO {

  private val simpleParser = People.id ~ People.name ~ Addresses.street.? map flatten

  private val peopleWithAddressParser = (People.id ~ People.name ~ People.age ~ Addresses.street.? ~ str("city_name").?).map {
    case id ~ name ~ age ~ Some(street) ~ Some(city) => (id, Person(name, age, Nil), Some(Address(street, city)))
    case id ~ name ~ age ~ _ ~ _ => (id, Person(name, age, Nil), None)
  }

  private val peopleWithAddressParserForComprehension = for {
    id <- People.id
    name <- People.name
    age <- People.age
    streetOpt <- Addresses.street.?
    cityOpt <- str("city_name").?
  } yield {
    val person = Person(name, age, Nil)
    (streetOpt, cityOpt) match {
      case (Some(street), Some(city)) => (id, person, Some(Address(street, city)))
      case _ => (id, person, None)
    }
  }

  def peopleWithAddresses: Iterable[Person] = DB.withConnection { implicit c =>
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
      Person(person.name, person.age, addresses)
    }

    peopleWithAddresses
  }
}
