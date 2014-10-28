package pl.learning.sprayio.api.gathering

trait GatheringMessage
case object GetResponse extends GatheringMessage
case class ResponseA(value: String) extends GatheringMessage
case class ResponseB(value: String) extends GatheringMessage
case class ResponseC(value: String) extends GatheringMessage