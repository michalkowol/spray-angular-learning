package pl.learning.db.slick.dto

import pl.learning.db.{AddressPerson, Person, Address, City}
import slick.driver.PostgresDriver.api._

// scalastyle:off
object Tables {
  class Cities(tag: Tag) extends Table[City](tag, "cities") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (name, id.?) <> (City.tupled, City.unapply)
  }
  val cities = TableQuery[Cities]

  class Addresses(tag: Tag) extends Table[Address](tag, "addresses") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def street = column[String]("street")
    def cityId = column[Int]("city_id")
    def * = (street, cityId, id.?) <> (Address.tupled, Address.unapply)
    def city = foreignKey("city_fk", cityId, cities)(_.id)
  }
  val addresses = TableQuery[Addresses]

  class People(tag: Tag) extends Table[Person](tag, "people") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def age = column[Int]("age")
    def * = (name, age, id.?) <> (Person.tupled, Person.unapply)
  }
  val people = TableQuery[People]

  class AddressesPeople(tag: Tag) extends Table[AddressPerson](tag, "addresses_people") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def personId = column[Int]("person_id")
    def addressId = column[Int]("address_id")
    def * = (personId, addressId, id.?) <> (AddressPerson.tupled, AddressPerson.unapply)
    def person = foreignKey("person_fk", personId, people)(_.id)
    def address = foreignKey("address_fk", addressId, addresses)(_.id)
  }
  val addressesPeople = TableQuery[AddressesPeople]
}
// scalastyle:on
