package pl.learning.sprayio.api

trait RestMessage

sealed trait ErrorMessage
case class Error(message: String) extends ErrorMessage
case class RequestTimeoutError(message: String = "Request Timeout") extends ErrorMessage
case class ValidationError(message: Map[String, String]) extends ErrorMessage
object Error {
  def apply(throwable: Throwable): Error = Error(throwable.getMessage)
}