package pl.learning.db.slick.dao

import pl.learning.db.{Address, City}
import pl.learning.db.slick.dto._
import pl.learning.db.slick.dto.Tables._
import pl.learning.db.slick.dto.Database._

import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

object AddressesDAO {

  def getByName(street: String, city: String): Future[Option[(Address, City)]] = {
    val query = for {
      a <- addresses if a.street === street
      c <- a.city
    } yield {
      (a, c)
    }
    db.run(query.result.headOption)
  }

  def getByName(street: String, city: City): Future[Option[Address]] = {
    val query = for {
      a <- addresses if a.street === street && a.cityId === city.id
    } yield {
      a
    }
    db.run(query.result.headOption)
  }

  def getOrCreate(street: String, city: String)(implicit ec: ExecutionContext): Future[(Address, City)] = {
    val cityObject = CitiesDAO.getOrCreate(city)
    cityObject.flatMap { city =>
      val address = getByName(street, city)
      address.flatMap {
        case Some(address) => Future { (address, city) }
        case None => createAddress(street, city.id.get).map(address => (address, city))
      }
    }
  }

  def createAddress(street: String, cityId: Int): Future[Address] = {
    val insert = addresses.map(a => (a.street, a.cityId)).returning(addresses) += (street, cityId)
    db.run(insert)
  }
}
