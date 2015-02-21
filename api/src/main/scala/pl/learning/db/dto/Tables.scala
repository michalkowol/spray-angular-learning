package pl.learning.db.dto

import slick.driver.PostgresDriver.api._

object Tables {
  case class City(name: String, id: Option[Int] = None)
  class Cities(tag: Tag) extends Table[City](tag, "cities") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (name, id.?) <> (City.tupled, City.unapply)
  }
  val cities = TableQuery[Cities]

  case class Address(street: String, cityId: Int, id: Option[Int] = None)
  class Addresses(tag: Tag) extends Table[Address](tag, "addresses") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def street = column[String]("street")
    def cityId = column[Int]("city_id")
    def * = (street, cityId, id.?) <> (Address.tupled, Address.unapply)
    def city = foreignKey("city_fk", cityId, cities)(_.id)
  }
  val addresses = TableQuery[Addresses]

  case class Person(name: String, age: Int, id: Option[Int] = None)
  class People(tag: Tag) extends Table[Person](tag, "people") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def age = column[Int]("age")
    def * = (name, age, id.?) <> (Person.tupled, Person.unapply)
  }
  val people = TableQuery[People]

  type AddressPerson = (Int, Int, Int)
  class AddressesPeople(tag: Tag) extends Table[AddressPerson](tag, "addresses_people") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def personId = column[Int]("person_id")
    def addressId = column[Int]("address_id")
    def * = (id, personId, addressId)
    def person = foreignKey("person_fk", personId, people)(_.id)
    def address = foreignKey("address_fk", addressId, addresses)(_.id)
  }
  val addressesPeople = TableQuery[AddressesPeople]
}
