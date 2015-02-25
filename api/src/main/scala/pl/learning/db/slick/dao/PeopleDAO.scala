package pl.learning.db.slick.dao

import pl.learning.db.slick.dto._
import pl.learning.db.slick.dto.Tables._
import pl.learning.db.slick.dto.Database._

import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

object PeopleDAO {

  private val findById = people.findBy(_.id)

  private type FullAddress = (Address, City)

  def getPersonAndAddressById(id: Int)(implicit ec: ExecutionContext): Future[Option[(Person, Seq[FullAddress])]] = {
    val query = for {
      ap <- addressesPeople if ap.personId === id
      p <- ap.person
      a <- ap.address
      c <- a.city
    } yield {
      (p, a, c)
    }
    db.run(query.result).map(mapToPersownWithAddress)
  }

  private def mapToPersownWithAddress(rows: Seq[(Person, Address, City)]): Option[(Person, Seq[FullAddress])] = {
    val addresses = rows.map { case (_, a, c) => (a, c) }
    rows.headOption.map { case (p, _, c) => (p, addresses) }
  }

  def getPersonById(id: Int): Future[Option[Person]] = {
    db.run(findById(id).result.headOption)
  }

  def nameById(id: Int)(implicit ec: ExecutionContext): Future[Option[String]] = {
    getPersonById(id).map(_.map(_.name))
  }

  def nameByIdSQL(id: Int): Future[Option[String]] = {
    val nameSQL = sql"select p.name from people as p where p.id = $id".as[String]
    db.run(nameSQL.headOption)
  }

  def over18: Future[Seq[Person]] = {
    db.run(findOverAgeQuery(18).result)
  }

  def under18: Future[Seq[Person]] = {
    db.run(findUnderAgeQuery(18).result)
  }

  private def findOverAgeQuery(age: Int) = people.filter(_.age >= age)
  private def findUnderAgeQuery(age: Int) = people.filter(_.age <= age)

  def list: Future[Seq[Person]] = {
    db.run(people.result)
  }

  def createPerson(name: String, age: Int): Future[Person] = {
    val insert = people.map(p => (p.name, p.age)).returning(people) += (name, age)
    db.run(insert)
  }

  def delete(id: Int): Future[_] = {
    db.run(findById(id).delete)
  }
}
