package pl.learning.sprayio

import slick.driver.PostgresDriver.api._

object SlickTables {
  type City = (Int, String)
  class Cities(tag: Tag) extends Table[City](tag, "CITIES") {
    def id = column[Int]("ID", O.PrimaryKey)
    def name = column[String]("NAME")
    def * = (id, name)
  }
  val cities = TableQuery[Cities]

  type Address = (Int, String, Int)
  class Addresses(tag: Tag) extends Table[Address](tag, "ADDRESSES") {
    def id = column[Int]("ID", O.PrimaryKey)
    def street = column[String]("STREET")
    def cityId = column[Int]("CITY_ID")
    def * = (id, street, cityId)
    def city = foreignKey("CITY_FK", cityId, cities)(_.id)
  }
  val addresses = TableQuery[Addresses]

  type Person = (Int, String, Int)
  class People(tag: Tag) extends Table[Person](tag, "PEOPLE") {
    def id = column[Int]("ID", O.PrimaryKey)
    def name = column[String]("NAME")
    def age = column[Int]("AGE")
    def * = (id, name, age)
  }
  val people = TableQuery[People]

  type AddressPerson = (Int, Int, Int)
  class AddressesPeople(tag: Tag) extends Table[AddressPerson](tag, "ADDRESSES_PEOPLE") {
    def id = column[Int]("ID", O.PrimaryKey)
    def personId = column[Int]("PERSON_ID")
    def addressId = column[Int]("ADDRESS_ID")
    def * = (id, personId, addressId)
    def person = foreignKey("PERSON_FK", personId, people)(_.id)
    def address = foreignKey("ADDRESS_FK", addressId, addresses)(_.id)
  }
  val addressesPeople = TableQuery[AddressesPeople]
}

