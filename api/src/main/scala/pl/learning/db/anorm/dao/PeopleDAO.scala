package pl.learning.db.anorm.dao

import anorm._
import anorm.SqlParser._
import pl.learning.db.anorm.DB
import pl.learning.sprayio.api.contentnegotiation.ContentNegotiation.{Address, Person}

object PeopleDAO {

  private val simpleParser = int("id") ~ str("name") ~ str("street").? map flatten

  private val peopleWithAddressParser = (int("id") ~ str("name") ~ int("age") ~ str("street").? ~ str("city").?).map {
    case id ~ name ~ age ~ Some(street) ~ Some(city) => (id, Person(name, age, Nil), Some(Address(street, city)))
    case id ~ name ~ age ~ _ ~ _ => (id, Person(name, age, Nil), None)
  }

  def peopleWithAddresses: Iterable[Person] = DB.withConnection { implicit c =>
    val sql =
      SQL"""
         SELECT p.id AS id, p.name AS name, p.age AS age, a.street AS street, c.name AS city FROM people AS p
         LEFT JOIN addresses_people AS ap ON ap.person_id = p.id
         LEFT JOIN addresses AS a ON a.id = ap.address_id
         LEFT JOIN cities AS c ON c.id = a.city_id
      """
    val peopleWithAddresses = sql.as(peopleWithAddressParser.*).groupBy(_._1).map {
      case (id, personWithAddress) =>
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
