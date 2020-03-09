package com.fragnostic.dao.crud

import java.sql.{ Connection, PreparedStatement }

import com.fragnostic.dao.support.{ ConnectionAgnostic, PreparedStatementSupport }
import org.slf4j.{ Logger, LoggerFactory }

/**
 * Created by Fernando Brule on 30-06-2015 22:23:00.
 * Generated by Tesseract.
 */
trait ExistsByAgnostic extends ConnectionAgnostic with PreparedStatementSupport {

  private[this] val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  //
  // EXISTS BY
  //
  def existsBy[P](
    param: P,
    sqlExistsBy: String,
    filloutPsExistsBy: (PreparedStatement, P) => Either[String, PreparedStatement]): Either[String, Boolean] =
    getConnection map (connection => {
      if (logger.isInfoEnabled) logger.info(s"existsBy | enter")
      existsBy(connection, param, sqlExistsBy, filloutPsExistsBy) fold (
        error => {
          logger.error(s"existsBy|$error")
          closeWithoutCommit(connection)
          Left(s"existsBy | 1, $error")
        },
        exists => {
          if (logger.isInfoEnabled) logger.info(s"existsBy | success, affectedRows: $exists")
          closeWithoutCommit(connection)
          Right(exists)
        })
    }) getOrElse Left("existsBy | 2, error.trying.to.get.db.connection")

  //
  // EXISTS BY
  //
  def existsBy[P](
    connection: Connection,
    param: P,
    sqlExistsBy: String,
    filloutPsExistsBy: (PreparedStatement, P) => Either[String, PreparedStatement]): Either[String, Boolean] = {

    if (logger.isInfoEnabled) logger.info(s"existsBy | enter, about to prepare statement...")
    val prepStat = connection.prepareStatement(sqlExistsBy)
    if (logger.isInfoEnabled) logger.info(s"existsBy | statement ready")
    filloutPsExistsBy(prepStat, param)
    executeQuery(prepStat) fold (
      error => {
        close(prepStat)
        logger.error(s"existsBy|$error")
        Left(s"existsBy | 4, error.executeQuery")
      },
      resultSet =>
        if (resultSet.next()) {
          if (logger.isInfoEnabled) logger.info(s"existsBy | have next")
          val ans = resultSet.getInt("num_ocurrencias") >= 1
          close(resultSet, prepStat)
          Right(ans)
        } else {
          if (logger.isInfoEnabled) logger.info(s"existsBy | doesnt have next")
          close(resultSet, prepStat)
          Right(false)
        })

  }

}