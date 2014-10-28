package pl.learning.sprayio

import pl.learning.sprayio.api.RestMessage

case class ResponseABC(valueA: String, valueB: String, valueC: String) extends RestMessage
case object GetResponseABC extends RestMessage