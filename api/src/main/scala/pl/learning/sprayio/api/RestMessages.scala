package pl.learning.sprayio.api

object Error {
  def apply(throwable: Throwable): Error = Error(throwable.getMessage)
}
case class Error(message: String)
case class Validation(message: String)
case class NotFound(message: String)
