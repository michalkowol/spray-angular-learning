package pl.learning.sprayio

trait RestMessage

case object GetResponseABC extends RestMessage
case class ResponseABC(valueA: String, valueB: String, valueC: String) extends RestMessage

case class Error(message: String)
case class Validation(message: String)

case object PetOverflowException extends Exception("PetOverflowException: OMG. Pets. Everywhere.")