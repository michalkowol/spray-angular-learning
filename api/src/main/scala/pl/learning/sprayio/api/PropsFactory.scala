package pl.learning.sprayio.api

import akka.actor.{ActorRef, Props}

trait PropsFactory {
  def props(originalSender: ActorRef): Props
}
