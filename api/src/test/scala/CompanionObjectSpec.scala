import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, FlatSpec}
import org.mockito.Mockito._

object CompanionObjectSpec {

  object MyClass {
    def someValue: String = "aaa"
  }
  class MyClass {
    val myClass = MyClass
    def foo: String = myClass.someValue
  }
}

class CompanionObjectSpec extends FlatSpec with Matchers with MockitoSugar {
  "Mockito" should "mock companion objects" in {
    // given
    val myClassObject = mock[CompanionObjectSpec.MyClass.type]
    when(myClassObject.someValue).thenReturn("bbb")
    val myClass = new CompanionObjectSpec.MyClass {
      override val myClass = myClassObject
    }

    // when
    val foo = myClass.foo

    // then
    foo shouldBe "bbb"
  }
}
