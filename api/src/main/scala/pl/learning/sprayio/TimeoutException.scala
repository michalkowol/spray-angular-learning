package pl.learning.sprayio

case class TimeoutException(msg: String = "Timeout") extends Exception(msg)
