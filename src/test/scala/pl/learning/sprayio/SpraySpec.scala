package pl.learning.sprayio

import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import spray.testkit.ScalatestRouteTest
import spray.routing.Directives
import spray.http.{StatusCodes, MediaTypes}

class SpraySpec extends FlatSpec with Matchers with MockitoSugar with ScalatestRouteTest with Directives {

  "An aquarium" should "get greeting message" in {
    Get("/hello") ~> Aquarium.fishRoute ~> check {
      responseAs[String] should include ("aquarium")
    }
  }

  it  should "get water level info" in {
    Get("/waterlevel") ~> Aquarium.waterRoute ~> check {
      responseAs[String] should include ("The water level is")
    }
  }

  it  should "get all fishes as JSON" in {
    Get("/all/withJson") ~> Aquarium.fishRoute ~> check {
      mediaType should be (MediaTypes.`application/json`)
      status.intValue should be (200)
      responseAs[String] should include ("Tuna")
      responseAs[String] should include ("pacific")
      responseAs[String] should include ("atlantic")
    }
  }

  it  should "get store-products.json" in {
    Get("/api/products") ~> Aquarium.staticResources ~> check {
      responseAs[String] should include ("Bloodstone")
    }
  }
}
