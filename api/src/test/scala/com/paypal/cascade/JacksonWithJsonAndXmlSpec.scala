package com.paypal.cascade

import org.scalatest.{Matchers, FlatSpec}
import com.paypal.cascade.json._
import com.paypal.cascade.xml._
import com.paypal.cascade.common.option._

object JacksonWithJsonAndXmlSpec {
  case class Person(name: String, age: Int, username: Option[String])
  case class City(addresses: Seq[Address])
  case class Address(street: String)
}

class JacksonWithJsonAndXmlSpec extends FlatSpec with Matchers {

  import JacksonWithJsonAndXmlSpec._

  val person = new Person("michal", 25, "kowolm".some)

  "A Jackson" should "marshal Person to JSON" in {
    // when
    val personJson = person.toJson.get
    // then
    personJson shouldBe """{"name":"michal","age":25,"username":"kowolm"}"""
  }
  it should "marshal Person to XML" in {
    // when
    val personXml = person.toXml.get
    // then
    personXml shouldBe """<Person><name>michal</name><age>25</age><username><username>kowolm</username></username></Person>"""
  }
  it should "unmarshal Person from XML" in {
    // given
    val personXml = """<person><name>michal</name><age>25</age><username>kowolm</username></person>"""
    // when
    val personFromXml = personXml.fromXml[Person].get
    // then
    personFromXml shouldBe person
  }
  it should "unmarshal Person from JSON" in {
    // given
    val personJson = """{"name":"michal","age":25,"username":"kowolm"}"""
    // when
    val personFromJson = personJson.fromJson[Person].get
    // then
    personFromJson shouldBe person
  }
}
