package pl.learning.com

import org.scalatest.{Matchers, FlatSpec}
import com.paypal.cascade.json._
import com.paypal.cascade.common.option._

object CascadeSpec {
  case class Inner(a: Int, b: Option[String])
  case class Outer(a: Int, b: String, c: Inner)
  case class OuterWithList(a: Int, b: String, c: Seq[Inner])
  class MyClass(val a: Int, b: Int, val opt: Option[Int], val none: Option[Int]) {
    val c = 8
    var d = 10
    private val e = 9
    def f = 9
    val g = "aaa".some
  }
}

class CascadeSpec extends FlatSpec with Matchers {

  import CascadeSpec._

  "Cascade" should "convert case class to JSON" in {
    // given
    val inner = Inner(3, "bbb".some)
    // when
    val json = inner.toJson.get
    // then
    json shouldBe """{"a":3,"b":"bbb"}"""
  }

  it should "convert JSON to case class" in {
    // given
    val json = """{"a":3,"b":"bbb"}"""
    // when
    val inner = json.fromJson[Inner].get
    // then
    inner shouldBe Inner(3, "bbb".some)
  }

  it should "convert case class with none to JSON" in {
    // given
    val inner = Inner(3, none)
    // when
    val json = inner.toJson.get
    // then
    json shouldBe """{"a":3}"""
  }

  it should "convert JSON to case class with none" in {
    // given
    val json = """{"a":3}"""
    // when
    val inner = json.fromJson[Inner].get
    // then
    inner shouldBe Inner(3, none)
  }

  it should "convert nested case classes to JSON" in {
    // given
    val outer = Outer(1, "aaa", Inner(3, "bbb".some))
    // when
    val json = outer.toJson.get
    // then
    json shouldBe """{"a":1,"b":"aaa","c":{"a":3,"b":"bbb"}}"""
  }

  it should "convert JSON to nested case classes" in {
    // given
    val json = """{"a":1,"b":"aaa","c":{"a":3,"b":"bbb"}}"""
    // when
    val outer = json.fromJson[Outer].get
    // then
    outer shouldBe Outer(1, "aaa", Inner(3, "bbb".some))
  }

  it should "convert case class with list to JSON" in {
    // given
    val outer = OuterWithList(1, "aaa", Seq(Inner(3, "bbb".some), Inner(7, "ccc".some)))
    // when
    val json = outer.toJson.get
    // then
    json shouldBe """{"a":1,"b":"aaa","c":[{"a":3,"b":"bbb"},{"a":7,"b":"ccc"}]}"""
  }

  it should "convert JSON to case class with list" in {
    // given
    val json = """{"a": 1, "b": "aaa", "c": [{"a": 3, "b": "bbb"}, {"a":7,"b":"ccc"}]}"""
    // when
    val outer = json.fromJson[OuterWithList].get
    // then
    outer shouldBe OuterWithList(1, "aaa", Seq(Inner(3, "bbb".some), Inner(7, "ccc".some)))
  }

  it should "convert normal class to JSON" in {
    // given
    val myClass = new MyClass(1, 2, 6.some, none)
    // when
    val json = myClass.toJson.get
    // then
    json shouldBe """{"a":1,"opt":6,"c":8,"d":10,"g":"aaa"}"""
  }
}
