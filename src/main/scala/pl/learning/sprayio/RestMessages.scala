package pl.learning.sprayio

trait RestMessage

case object GetResponseABC extends RestMessage
case class ResponseABC(valueA: String, valueB: String, valueC: String) extends RestMessage

case class Error(message: String)
case class Validation(message: String)

case object FooException extends Exception("Foo bar baz")