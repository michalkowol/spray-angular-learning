package pl.learning.sprayio.api

trait RestMessage

case class Error(message: String)
case class Validation(message: String)