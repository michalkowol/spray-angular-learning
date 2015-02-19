package pl.learning.sprayio.api.sameactorproblem

import akka.actor.Props
import pl.learning.sprayio.api.gathering.MessageProvider
import pl.learning.sprayio.api.pattern.PerRequestCreator
import spray.routing.{Route, HttpService}

import scala.concurrent.duration._

trait AggregatorHttpService extends HttpService with PerRequestCreator {

  private val serviceA = actorRefFactory.actorOf(Props[MessageProvider])

  def aggregateRoute: Route = get {
    path("aggregateSameMessages") {
      aggregate {
        Aggregator.Aggregate
      }
    }
  }

  private def aggregate(message: Any): Route = { ctx =>
    perRequest(ctx, AggregatorRefMaker(serviceA), message, timeout = 500.milliseconds)
  }
}
