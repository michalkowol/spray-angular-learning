package pl.learning.sprayio.api.divide

import pl.learning.sprayio.api.RestMessage

case class DivideNumbers(a: Int, b: Int) extends RestMessage
case class DivideResult(result: Int, successCount: Int) extends RestMessage