package com.fragnostic.dao.crud

import java.sql.{ Connection, PreparedStatement, ResultSet }

import com.fragnostic.dao.support.{ CloseResourceAgnostic, ConnectionAgnostic, PreparedStatementSupport }
import org.slf4j.{ Logger, LoggerFactory }

/**
 * Created by Fernando Brule on 30-06-2015 22:23:00.
 * Generated by Tesseract.
 */
trait FindByAgnostic extends ConnectionAgnostic with CloseResourceAgnostic with PreparedStatementSupport {

  private[this] val logger: Logger = LoggerFactory.getLogger("FindByAgnostic")

  //
  // Find By
  //
  def findBy[P, T](
    parameter: P,
    sqlFindBy: String,
    filloutPsFindBy: (PreparedStatement, P) => Either[String, PreparedStatement],
    newEntity: (ResultSet, Map[String, String]) => Either[String, T],
    args: Map[String, String] = Map.empty): Either[String, Option[T]] =
    getConnection map (connection =>
      findBy(
        connection,
        parameter,
        sqlFindBy,
        filloutPsFindBy,
        newEntity,
        args) fold (
        error => {
          logger.error(s"findBy | 1 - $error")
          closeWithoutCommit(connection)
          Left(error)
        }, opt => {
          closeWithoutCommit(connection)
          Right(opt)
        })) getOrElse Left("find.by.agnostic.error.no.db.connection")

  //
  // Find By
  //
  def findBy[P, T](
    connection: Connection,
    parameter: P,
    sqlFindBy: String,
    filloutPsFindBy: (PreparedStatement, P) => Either[String, PreparedStatement],
    newEntity: (ResultSet, Map[String, String]) => Either[String, T],
    args: Map[String, String]): Either[String, Option[T]] =
    prepareStatement(connection, sqlFindBy) fold (
      error => {
        logger.error(s"findBy | error on prepareStatement - $error")
        Left("find.by.agnostic.error.1")
      },
      prepStat =>
        filloutPsFindBy(prepStat, parameter) fold (
          error => {
            close(prepStat)
            logger.error(s"findBy | error on filloutPsFindBy: $error")
            Left("find.by.agnostic.error.2")
          },
          prepStat =>
            executeQuery(prepStat) fold (
              error => {
                logger.error(s"findBy | error on executeQuery - $error")
                close(prepStat)
                Left("find.by.agnostic.error.3")
              },
              resultSet =>
                if (resultSet.next()) {
                  newEntity(resultSet, args) fold (
                    error => {
                      logger.error(s"findBy() - $error")
                      Left("find.by.agnostic.error.4")
                    },
                    entity => {
                      close(resultSet, prepStat)
                      Right(Some(entity))
                    })
                } else {
                  close(resultSet, prepStat)
                  Right(None)
                })))

}
