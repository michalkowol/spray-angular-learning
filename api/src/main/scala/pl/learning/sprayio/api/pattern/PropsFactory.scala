package pl.learning.sprayio.api.pattern

import akka.actor.{ActorRef, Props}

trait PropsFactory {
  def props(originalSender: ActorRef): Props
}
