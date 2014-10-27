package pl.learning.sprayio.perrequest2

trait RestMessage

case class Error(message: String)
case class Validation(message: String)