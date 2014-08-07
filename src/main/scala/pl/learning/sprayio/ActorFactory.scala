package pl.learning.sprayio

import akka.actor.{Props, ActorRef}

trait ActorFactory {
  def build(originalSender: ActorRef): Props
}
