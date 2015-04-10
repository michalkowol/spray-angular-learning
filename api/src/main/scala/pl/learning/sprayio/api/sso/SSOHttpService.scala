package pl.learning.sprayio.api.sso

import akka.actor.Props
import pl.learning.sprayio.api.pattern.PerRequestCreator
import spray.routing.{HttpService, Route}

import scala.concurrent.duration._

trait SSOHttpService extends HttpService with PerRequestCreator {

  private val ssoActor = actorRefFactory.actorOf(Props[SSOActor], "SSOActor")

  def ssoRoute: Route = get {
    path("sso") {
      cookie("fb") { cookie =>
        getHealth {
          SSOActor.GetProfile(cookie.content)
        }
      }
    }
  }

  private def getHealth(message: Any): Route = { ctx =>
    println(ctx.request.headers)
    perRequest(ctx, ssoActor, message, 10000.milliseconds)
  }
}
