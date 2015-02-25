package pl.learning.db.anorm

import java.sql.Connection

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariDataSource

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.ControlThrowable
import javax.sql.DataSource

object DB {

  private val config = ConfigFactory.load()

  lazy val dataSource: DataSource = {
    val ds = new HikariDataSource()
    ds.setJdbcUrl(config.getString("db.default.url"))
    ds.setUsername(config.getString("db.default.user"))
    ds.setPassword(config.getString("db.default.password"))
    ds
  }

  def getConnection(): Connection = {
    getConnection(autocommit = true)
  }

  def getConnection(autocommit: Boolean): Connection = {
    val connection = dataSource.getConnection
    connection.setAutoCommit(autocommit)
    connection
  }

  def withConnection[A](block: Connection => A): A = {
    withConnection(autocommit = true)(block)
  }

  def withConnection[A](autocommit: Boolean)(block: Connection => A): A = {
    val connection = getConnection(autocommit)
    try {
      block(connection)
    } finally {
      connection.close()
    }
  }

  def withAsyncConnection[A](block: Connection => A)(implicit ec: ExecutionContext): Future[A] = {
    withAsyncConnection(autocommit = true)(block)
  }

  def withAsyncConnection[A](autocommit: Boolean)(block: Connection => A)(implicit ec: ExecutionContext): Future[A] = {
    Future { withConnection(autocommit = true)(block) }
  }

  def withTransaction[A](block: Connection => A): A = {
    withConnection(autocommit = false) { connection =>
      try {
        val r = block(connection)
        connection.commit()
        r
      } catch {
        case e: ControlThrowable =>
          connection.commit()
          throw e
        case e: Throwable =>
          connection.rollback()
          throw e
      }
    }
  }

  def withAsyncTransaction[A](block: Connection => A)(implicit ec: ExecutionContext): Future[A] = {
    Future { withTransaction(block) }
  }
}
