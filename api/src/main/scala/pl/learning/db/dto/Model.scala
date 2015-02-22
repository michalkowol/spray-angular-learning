package pl.learning.db.dto

case class City(id: Int, name: String)
case class Address(id: Int, street: String, cityId: Int)
case class Person(id: Int, name: String, age: Int)
case class AddressPerson(id: Int, personId: Int, addressId: Int)
