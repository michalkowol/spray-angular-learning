package pl.learning.db.slick.dao

import pl.learning.db.Person
import pl.learning.db.slick.dto._
import pl.learning.db.slick.dto.Tables._
import slick.driver.PostgresDriver.api._
import scala.language.higherKinds

// scalastyle:off
object TablesExtensions {

  implicit class PersonExtensions[C[_]](q: Query[People, Person, C]) {
    def withAddress = for {
      p <- q
      ap <- addressesPeople if p.id === ap.personId
      a <- ap.address
      c <- a.city
    } yield (p, a, c)
  }
}
// scalastyle:on
