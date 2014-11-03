package pl.learning.sprayio.api.health

import pl.learning.sprayio.api.RestMessage

case class Health(status: String) extends RestMessage
case object GetHealth extends RestMessage