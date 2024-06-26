package com.fragnostic.dao.crud

import com.fragnostic.dao.support.{ ConnectionAgnostic, PreparedStatementSupport, RecursionSupport }
import org.slf4j.{ Logger, LoggerFactory }

import java.sql.{ Connection, ResultSet }

/**
 * Created by fernandobrule on 7/21/16.
 */
trait FindListAgnostic extends ConnectionAgnostic with PreparedStatementSupport with RecursionSupport {

  private[this] val logger: Logger = LoggerFactory.getLogger("FindListAgnostic")

  //
  // Find List By Id
  //
  def findList[T](
    sqlFindListBy: String,
    newEntity: (ResultSet, Map[String, String]) => Either[String, T],
    args: Map[String, String] = Map.empty): Either[String, List[T]] = {
    getConnection map (
      connection => findList(connection, sqlFindListBy, newEntity, args) fold (
        error => {
          closeWithoutCommit(connection)
          Left(error)
        },
        list => {
          closeWithoutCommit(connection)
          Right(list)
        } //
      ) //
    ) getOrElse Left("find.by.agnostic.error.no.db.connection") //
  }

  //
  // Find List By Id
  //
  def findList[T](
    connection: Connection,
    sqlFindListBy: String,
    newEntity: (ResultSet, Map[String, String]) => Either[String, T],
    args: Map[String, String]): Either[String, List[T]] = {
    prepareStatement(connection, sqlFindListBy) fold (
      error => {
        logger.error(
          s"find.list.by.agnostic.error.on.prep.stat,\n\t- sqlFindBy: $sqlFindListBy")
        Left(s"find.list.by.agnostic.error.on.prep.stat")
      },
      prepStat =>
        executeQuery(prepStat) fold (
          error => {
            close(prepStat)
            logger.error(
              s"find.list.by.agnostic.error.on.exec.query,\n\t- sqlFindBy: $sqlFindListBy")
            Left(s"find.list.by.agnostic.error.on.exec.query")
          },
          resultSet => {

            Right(newList(resultSet, newEntity, args))

          } //
        ) //
    )
  }

}
