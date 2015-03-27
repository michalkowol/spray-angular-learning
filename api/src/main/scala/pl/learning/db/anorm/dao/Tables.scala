package pl.learning.db.anorm.dao

import anorm.SqlParser._
import pl.learning.db.{AddressPerson, Address, City, Person}

object People {
  val id = int("id")
  val name = str("name")
  val age = int("age")

  val person = name ~ age ~ id.? map flatten map Person.tupled
}

object Addresses {
  val id = int("id")
  val street = str("street")
  val cityId = int("city_id")

  val address = street ~ cityId ~ id.? map flatten map Address.tupled
}

object Cities {
  val id = int("id")
  val name = str("name")

  val city = name ~ id.? map flatten map City.tupled
}

object AddressesPeople {
  val id = int("id")
  val personId = int("person_id")
  val addressId = int("address_id")

  val addressPerson = personId ~ addressId ~ id.? map flatten map AddressPerson.tupled
}
