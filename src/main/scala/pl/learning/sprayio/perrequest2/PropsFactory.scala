package pl.learning.sprayio.perrequest2

import akka.actor.{ActorRef, Props}

trait PropsFactory {
  def build(originalSender: ActorRef): Props
}
