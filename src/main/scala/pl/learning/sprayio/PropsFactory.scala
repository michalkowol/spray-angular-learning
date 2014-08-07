package pl.learning.sprayio

import akka.actor.{ Props, ActorRef }

trait PropsFactory {
  def build(originalSender: ActorRef): Props
}
