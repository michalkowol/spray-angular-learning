package pl.learning.db.dao

import pl.learning.db.dto._
import pl.learning.db.dto.Tables._
import pl.learning.db.dto.Database._

import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

object CitiesDAO {

  private val findByNameQuery = cities.findBy(_.name)

  def findByName(name: String): Future[Option[City]] = {
    db.run(findByNameQuery(name).result.headOption)
  }

  def getOrCreate(name: String)(implicit ec: ExecutionContext): Future[City] = {
    val cityOption = findByName(name)
    cityOption.flatMap {
      case Some(city) => Future { city }
      case None => createCity(name)
    }
  }

  def createCity(name: String): Future[City] = {
    val insert = cities.map(c => c.name).returning(cities) += name
    db.run(insert)
  }
}
