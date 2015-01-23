package pl.learning.sprayio

case class TimeoutException(message: String = "Timeout Exception") extends Exception(message)
case class MessageNotSupported(message: String = "Message Not Supported") extends Exception(message)
