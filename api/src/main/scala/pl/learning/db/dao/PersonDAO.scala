package pl.learning.db.dao

import pl.learning.db.dto.Tables._
import pl.learning.db.dto.Database._

import slick.driver.PostgresDriver.api._
import com.paypal.cascade.common.option._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object PersonDAO {

  private val findById = people.findBy(_.id)

  private type FullAddress = (Address, City)

  def getPersonAndAddressById(id: Int): Future[(Person, Seq[FullAddress])] = {
    val query = for {
      ap <- addressesPeople if ap.personId === id
      p <- ap.person
      a <- ap.address
      c <- a.city
    } yield {
      (p, a, c)
    }
    db.run(query.result)
      .map(mapToPersownWithAddress)
      .flatMap(_.toFuture(new NotFoundException(s"Person with id $id do not have full address")))
  }

  private def mapToPersownWithAddress(rows: Seq[(Person, Address, City)]): Option[(Person, Seq[FullAddress])] = {
    val addresses = rows.map { case (_, a, c) => (a, c) }
    rows.headOption.map { case (p, _, c) => (p, addresses) }
  }

  def getPersonById(id: Int): Future[Person] = {
    db.run(findById(id).result.headOption).flatMap(p => personOptionToFuture(id, p))
  }

  def nameById(id: Int): Future[String] = {
    getPersonById(id).map(_.name)
  }

  def nameByIdSQL(id: Int): Future[String] = {
    val nameSQL = sql"select p.name from people as p where p.id = $id".as[String]
    db.run(nameSQL.headOption).flatMap(name => personOptionToFuture(id, name))
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

  def insetNewPerson(name: String, age: Int): Future[Person] = {
    val insert = people.map(p => (p.name, p.age)).returning(people) += (name, age)
    db.run(insert)
  }

  def delete(id: Int): Future[_] = {
    db.run(findById(id).delete)
  }

  private def optionToFuture[T](dataName: String, id: Int)(opt: Option[T]): Future[T] =
    opt.toFuture(new NotFoundSingleDataException(dataName, id))

  private def personOptionToFuture[T](id: Int, opt: Option[T]): Future[T] = optionToFuture("Person", id)(opt)
}
