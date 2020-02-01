package com.fragnostic.dao.crud

import java.sql.{ Connection, ResultSet }

import com.fragnostic.dao.support.{ ConnectionAgnostic, PreparedStatementSupport, RecursionSupport }
import org.slf4j.{ Logger, LoggerFactory }

/**
 * Created by fernandobrule on 7/21/16.
 */
trait FindListAgnostic extends ConnectionAgnostic with PreparedStatementSupport with RecursionSupport {

  private[this] val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  //
  // Find List By Id
  //
  def findList[T](
    sqlFindListBy: String,
    newEntity: ResultSet => Either[String, T]): Either[String, List[T]] =
    getConnection map (
      connection => findList(connection, sqlFindListBy, newEntity) fold (
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
  def findList[T](
    connection: Connection,
    sqlFindListBy: String,
    newEntity: ResultSet => Either[String, T]): Either[String, List[T]] =
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
            newList(resultSet, newEntity) fold (
              error => {
                logger.error(s"findList() - $error")
                close(resultSet, prepStat)
                Left(error)
              },
              list => {
                close(resultSet, prepStat)
                Right(list)
              })
          }))

}
