package pl.learning.db.slick.dto

import slick.driver.PostgresDriver.api.{Database => PSQLDatabase}

object Database {
  val db = PSQLDatabase.forConfig("db.default")
}
