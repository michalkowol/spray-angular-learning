package pl.learning.sprayio.api.sso

import akka.actor.Actor
import akka.event.LoggingReceive
import org.pac4j.core.context.Pac4jConstants
import org.pac4j.oauth.client.FacebookClient

import scala.util.Try

object SSOActor {
  case class Profile(status: String)
  case class GetProfile(accessToken: String)
}

class SSOActor extends Actor {
  import SSOActor._

  private val client = Try {
    val client = new FacebookClient("616457961788323", "5aafa2a476337e92b585148a66f0c472")
    client.setCallbackUrl("http://http://localhost:8081")
    client
  }

  def receive: Receive = LoggingReceive {
    case GetProfile(accessToken) =>
      val profile = client.get.getUserProfile(accessToken)
      sender() ! Profile(profile.getEmail)
  }
}
