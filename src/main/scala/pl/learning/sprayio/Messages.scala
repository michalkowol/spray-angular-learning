package pl.learning.sprayio

import pl.learning.sprayio.perrequest2.RestMessage

case class ResponseABC(valueA: String, valueB: String, valueC: String) extends RestMessage
case object GetResponseABC extends RestMessage