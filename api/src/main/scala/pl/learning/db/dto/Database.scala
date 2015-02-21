package pl.learning.db.dto

import slick.driver.PostgresDriver.api.{ Database => PSQLDatabase}

object Database {
  val db = PSQLDatabase.forConfig("db.psql.aws.yourcode")
}
