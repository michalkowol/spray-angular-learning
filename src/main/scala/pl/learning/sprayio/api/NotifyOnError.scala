package pl.learning.sprayio.api

import akka.actor.ActorContext
import akka.actor.Status.Failure

import scala.util.control.NonFatal

object NotifyOnError {
  def apply[T](unsafeCode: => T)(implicit context: ActorContext): T = {
    try {
      unsafeCode
    } catch {
      case NonFatal(e) =>
        context.sender ! Failure(e)
        throw e
    }
  }
}
