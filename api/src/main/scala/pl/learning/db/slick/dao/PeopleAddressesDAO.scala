package pl.learning.db.slick.dao

import pl.learning.db.slick.dto._
import pl.learning.db.slick.dto.Tables._
import pl.learning.db.slick.dto.Database._

import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext

import scala.concurrent.Future

object PeopleAddressesDAO {
  def createRelation(person: Person, address: Address)(implicit ec: ExecutionContext): Future[Int] = {
    val insert = addressesPeople.map(ap => (ap.personId, ap.addressId)).returning(addressesPeople) += (person.id, address.id)
    db.run(insert).map(_.id)
  }
}
