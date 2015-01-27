package pl.learning.sprayio.api.pattern

import akka.actor.{ActorRefFactory, ActorRef}

trait ActorRefMaker {
  def create(context: ActorRefFactory, originalSender: ActorRef): ActorRef
  def apply(context: ActorRefFactory, originalSender: ActorRef): ActorRef = create(context, originalSender)
}
