package com.fragnostic.dao

import java.sql.{ Connection, PreparedStatement, ResultSet }

import com.fragnostic.dao.support.{ ConnectionAgnostic, PreparedStatementSupport, RecursionSupport }
import org.slf4j.LoggerFactory

/**
 * Created by fernandobrule on 8/20/16.
 */
trait FindListByAgnostic extends ConnectionAgnostic with PreparedStatementSupport with RecursionSupport {

  private def logger = LoggerFactory.getLogger(getClass.getName)

  //
  // Find List By Id
  //
  def findListBy[P, T](
    parameter: P,
    sqlFindListBy: String,
    filloutPsFindListBy: (PreparedStatement, P) => Either[String, PreparedStatement],
    newEntity: ResultSet => T): Either[String, List[T]] =
    getConnection map (
      connection =>
        findListBy(
          connection,
          parameter,
          sqlFindListBy,
          filloutPsFindListBy,
          newEntity) fold (
          error => {
            closeWithoutCommit(connection)
            Left(error)
          },
          list => {
            closeWithoutCommit(connection)
            Right(list)
          })) getOrElse Left("find.by.agnostic.error.no.db.connection")

  //
  // Find List By Id
  //
  def findListBy[P, T](
    connection: Connection,
    parameter: P,
    sqlFindListBy: String,
    filloutPsFindListBy: (PreparedStatement, P) => Either[String, PreparedStatement],
    newEntity: ResultSet => T): Either[String, List[T]] =
    prepareStatement(connection, sqlFindListBy) fold (
      error => {
        logger.error(
          s"find.list.by.agnostic.error.on.prep.stat,\n\t- parameter: $parameter \n\t- sqlFindBy: $sqlFindListBy")
        Left(s"find.list.by.agnostic.error.on.prep.stat")
      },
      prepStat =>
        filloutPsFindListBy(prepStat, parameter) fold (
          error => {
            close(prepStat)
            logger.error(
              s"find.list.by.agnostic.error.on.fillout.prep.stat,\n\t- parameter: $parameter \n\t- sqlFindBy: $sqlFindListBy")
            Left(s"find.list.by.agnostic.error.on.fillout.prep.stat")
          },
          prepStat =>
            executeQuery(prepStat) fold (
              error => {
                close(prepStat)
                logger.error(
                  s"find.list.by.agnostic.error.on.exec.query,\n\t- parameter: $parameter \n\t- sqlFindBy: $sqlFindListBy")
                Left(s"find.list.by.agnostic.error.on.exec.query")
              },
              resultSet => {
                val list = newList(resultSet, newEntity)
                close(resultSet, prepStat)
                Right(list)
              })))

}
