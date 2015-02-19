package pl.learning.sprayio.api.pattern

import akka.actor.{ActorRef, ActorRefFactory}

trait ActorRefMaker {
  def create(context: ActorRefFactory, originalSender: ActorRef): ActorRef
  def apply(context: ActorRefFactory, originalSender: ActorRef): ActorRef = create(context, originalSender)
}
