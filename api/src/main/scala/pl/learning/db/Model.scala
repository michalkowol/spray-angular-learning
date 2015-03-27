package pl.learning.db

case class City(name: String, id: Option[Int] = None)
case class Address(street: String, cityId: Int, id: Option[Int] = None)
case class Person(name: String, age: Int, id: Option[Int] = None)
case class AddressPerson(personId: Int, addressId: Int, id: Option[Int] = None)
