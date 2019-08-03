package com.fragnostic.dao

import java.sql.{ Connection, PreparedStatement, ResultSet }

import com.fragnostic.dao.support.{ ConnectionAgnostic, PreparedStatementSupport }
import org.slf4j.LoggerFactory

/**
 * Created by Fernando Brule on 30-06-2015 22:23:00.
 * Generated by Tesseract.
 */
trait FindByIdAgnostic extends ConnectionAgnostic with PreparedStatementSupport {

  private def logger = LoggerFactory.getLogger(getClass.getName)

  //
  // Find By Id
  //
  def findById[I, T](
    entityId: I,
    sqlFindById: String,
    filloutPsFindById: (PreparedStatement, I) => Either[String, PreparedStatement],
    newEntity: ResultSet => T): Either[String, Option[T]] =
    getConnection map (connection =>
      findById(
        connection,
        entityId,
        sqlFindById,
        filloutPsFindById,
        newEntity) fold (
        error => {
          logger.error(s"findById | error: $error")
          closeWithoutCommit(connection)
          Left(error)
        }, opt => {
          closeWithoutCommit(connection)
          Right(opt)
        })) getOrElse Left("Error: trying to get DB connection")

  //
  // Find By Id
  //
  def findById[I, T](
    connection: Connection,
    entityId: I,
    sqlFindById: String,
    filloutPsFindById: (PreparedStatement, I) => Either[String, PreparedStatement],
    newEntity: ResultSet => T): Either[String, Option[T]] = {

    val prepStat = connection.prepareStatement(sqlFindById)
    filloutPsFindById(prepStat, entityId) fold (
      error => {
        logger.error(s"findById | error: $error")
        Left(error)
      },
      _ =>
        executeQuery(prepStat) fold (
          error => {
            logger.error(s"findById | error: $error")
            Left(error)
          },
          resultSet =>
            if (resultSet.next()) {
              try {
                val entity = newEntity(resultSet)
                close(resultSet, prepStat)
                Right(Some(entity))
              } catch {
                case e: Exception => {
                  logger.error(s"findById|$e")
                  Left(e.getMessage)
                }
              }
            } else {
              close(resultSet, prepStat)
              Right(None)
            }))

  }

}
