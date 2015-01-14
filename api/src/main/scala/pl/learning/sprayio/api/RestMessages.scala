package pl.learning.sprayio.api

sealed trait RestMessage
case class Error(message: String) extends RestMessage
case class Validation(message: String)