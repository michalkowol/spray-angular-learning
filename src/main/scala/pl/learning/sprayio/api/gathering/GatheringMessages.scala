package pl.learning.sprayio.api.gathering

sealed trait GatheringMessage

case object GetResponse extends GatheringMessage

case object GetResponseA extends GatheringMessage
case class ResponseA(value: String) extends GatheringMessage

case object GetResponseB extends GatheringMessage
case class ResponseB(value: String) extends GatheringMessage

case object GetResponseC extends GatheringMessage
case class ResponseC(value: String) extends GatheringMessage