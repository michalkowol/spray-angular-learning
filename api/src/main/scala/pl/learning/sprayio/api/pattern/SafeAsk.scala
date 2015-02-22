package pl.learning.sprayio.api.pattern

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorRef}
import akka.util.Timeout

import scala.concurrent.Future
import scala.reflect.{ClassTag, _}
import scala.util.control.NonFatal

import scala.language.higherKinds
import scala.language.implicitConversions

sealed trait ReplyAction[T]
case class ReplyWith[T](t: T) extends ReplyAction[T]
case class DoNotReply[T]() extends ReplyAction[T]

trait Replyable[T]

case class User(name: String)
sealed trait UserMsgs[T] extends Replyable[T]
case class LookupUser(id: Int) extends UserMsgs[Option[User]]
case object UserCount extends UserMsgs[Int]

trait ReplyingActor extends Actor {
  type M[X] <: Replyable[X]
  val ev: ClassTag[M[_]]

  def receive: Receive = {
    case m if ev.runtimeClass.isAssignableFrom(m.getClass) => {
      doReceiveAndReply(m.asInstanceOf[M[_]])
    }
  }

  private def doReceiveAndReply[T](msg: M[T]): Unit = {
    try {
      receiveAndReply(msg) match {
        case ReplyWith(t) => sender() ! t
        case DoNotReply() => // do nothing
      }
    } catch {
      case NonFatal(e) => sender() ! Failure(e)
    }
  }

  def receiveAndReply[T](msg: M[T]): ReplyAction[T]
}

trait ReplySupport {
  implicit class ReplyActorRef(actorRef: ActorRef) {
    // def ?
    def ask[T](message: Replyable[T])(implicit timeout: Timeout, tag: ClassTag[T]): Future[T] = {
      akka.pattern.ask(actorRef, message).mapTo[T]
    }
  }

  implicit def valueToReplyWith[T](t: T): ReplyWith[T] = ReplyWith(t)
}

class UserReplyingActor extends ReplyingActor {
  type M[T] = UserMsgs[T]
  val ev = classTag[M[_]]

  override def receiveAndReply[T](msg: UserMsgs[T]): ReplyAction[T] = msg match {
    case LookupUser(id) => ReplyWith(Some(User("michal")))
    case UserCount => ReplyWith(32)
  }
}
