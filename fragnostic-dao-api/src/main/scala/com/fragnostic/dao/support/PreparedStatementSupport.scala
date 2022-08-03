package com.fragnostic.dao.support

import java.sql.{ Connection, PreparedStatement, ResultSet, SQLException }

import org.slf4j.{ Logger, LoggerFactory }

/**
 * Created by fernandobrule on 7/21/16.
 */
trait PreparedStatementSupport extends PreparedStatementParamsSupport with CloseResourceAgnostic {

  private[this] val logger: Logger = LoggerFactory.getLogger("PreparedStatementSupport")

  def prepareStatement(
    connection: Connection,
    sql: String): Either[String, PreparedStatement] =
    try {
      Right(connection.prepareStatement(sql))
    } catch {
      case e: SQLException => {
        logger.error(s"prepareStatement() - $e\n$sql")
        Left("prepared.statement.support.error.on.prepare.statement.1")
      }
      case e: Throwable => {
        logger.error(s"prepareStatement() - $e\n$sql")
        Left("prepared.statement.support.error.on.prepare.statement.2")
      }
    }

  def executeQuery(prepStat: PreparedStatement): Either[String, ResultSet] =
    try {
      Right(prepStat.executeQuery())
    } catch {
      case e: SQLException => {
        logger.error(s"executeQuery() -1 $e")
        Left("prepared.statement.support.error.on.execute.query.1")
      }
      case e: Exception => {
        logger.error(s"executeQuery() -2 $e")
        Left("prepared.statement.support.error.on.execute.query.2")
      }
      case e: Throwable => {
        logger.error(s"executeQuery() -3 $e")
        Left("prepared.statement.support.error.on.execute.query.3")
      }
    }

  def executeUpdate(
    preparedStatement: PreparedStatement): Either[String, Int] =
    try {
      Right(preparedStatement.executeUpdate())
    } catch {
      case e: SQLException =>
        logger.error(s"executeUpdate() - $e")
        Left("statement.agnostic.error.execute.update.1")
      case e: Exception =>
        logger.error(s"executeUpdate() - $e")
        Left("statement.agnostic.error.execute.update.2")
      case e: Throwable =>
        logger.error(s"executeUpdate() - $e")
        Left("statement.agnostic.error.execute.update.3")
    }

  def executeQuery(
    connection: Connection,
    sql: String,
    optParams: Map[Int, (String, String)]): Either[List[String], ResultSet] =
    try {
      val prepStat: PreparedStatement = connection.prepareStatement(sql)
      setParams(optParams, prepStat) fold (
        errors => {
          close(prepStat)
          logger.error(s"executeQuery() - $errors\n$sql")
          Left(errors)
        },
        c => {
          val resultSet = prepStat.executeQuery()
          Right(resultSet)
        })

    } catch {
      case e: SQLException =>
        logger.error(s"executeQuery() - $e\n$sql")
        Left(List("statement.agnostic.error.execute.query.1"))
      case e: Exception =>
        logger.error(s"executeQuery() - $e\n$sql")
        Left(List("statement.agnostic.error.execute.query.2"))
      case e: Throwable =>
        logger.error(s"executeQuery() - $e\n$sql")
        Left(List("statement.agnostic.error.execute.query.3"))
    }

  def executeQuery(
    connection: Connection,
    sql: String): Either[List[String], ResultSet] =
    try {
      val prepStat: PreparedStatement = connection.prepareStatement(sql)
      val resultSet = prepStat.executeQuery()
      Right(resultSet)
    } catch {
      case e: SQLException =>
        logger.error(s"executeQuery() - $e\n$sql")
        Left(List("statement.agnostic.error.execute.query.1"))
      case e: Exception =>
        logger.error(s"executeQuery() - $e\n$sql")
        Left(List("statement.agnostic.error.execute.query.2"))
      case e: Throwable =>
        logger.error(s"executeQuery() - $e\n$sql")
        Left(List("statement.agnostic.error.execute.query.3"))
    }

}
