package com.fragnostic.dao

import java.sql.{ Connection, ResultSet, SQLException }

import com.fragnostic.dao.support.{ ConnectionAgnostic, PreparedStatementSupport, RecursionSupport }
import org.slf4j.LoggerFactory

/**
 * Created by fernandobrule on 7/21/16.
 */
trait FindListAgnostic extends ConnectionAgnostic with PreparedStatementSupport with RecursionSupport {

  private def logger = LoggerFactory.getLogger(getClass.getName)

  //
  // Find List By Id
  //
  def findList[T](
    sqlFindListBy: String,
    newEntity: ResultSet => T): Either[String, List[T]] =
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
    newEntity: (ResultSet) => T): Either[String, List[T]] =
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
          resultSet => try {
            val list = newList(resultSet, newEntity)
            close(resultSet, prepStat)
            Right(list)
          } catch {
            case e: SQLException => {
              close(resultSet, prepStat)
              logger.error(s"findList | $e")
              Left("find.list.by.agnostic.error.sql.exception")
            }
            case e: Exception => {
              close(resultSet, prepStat)
              logger.error(s"findList | $e")
              Left("find.list.by.agnostic.error.exception")
            }
          }))

}
