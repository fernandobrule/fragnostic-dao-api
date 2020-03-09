package com.fragnostic.dao.crud

import java.sql.{ Connection, PreparedStatement }

import com.fragnostic.dao.support.{ ConnectionAgnostic, PreparedStatementSupport }
import org.slf4j.{ Logger, LoggerFactory }

import scala.util.Try

/**
 * Created by Fernando Brule on 30-06-2015 22:23:00.
 * Generated by Tesseract.
 */
trait DeleteByIdAgnostic extends ConnectionAgnostic with PreparedStatementSupport {

  private[this] val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  //
  // Delete by Id
  //
  def deleteById[I](
    entityId: I,
    sqlDeleteById: String,
    filloutPsDelete: (PreparedStatement, I) => Either[String, PreparedStatement],
    validateAffRows: Option[Int] = Some(1)): Either[String, Int] =
    getConnection map (connection => {
      Try({
        deleteById(
          connection,
          entityId,
          sqlDeleteById,
          filloutPsDelete) fold (
          error => {
            logger.error(s"deleteById | $error")
            closeWithoutCommit(connection)
            Left(error)
          },
          affectedRows =>
            validateAffRows map (
              number =>
                if (affectedRows == number) {
                  closeWithCommit(connection)
                  Right(affectedRows)
                } else {
                  closeWithoutCommit(connection)
                  Left(
                    s"agnostic.delete.by.id.error.aff.rows.are.not.$number.was.$affectedRows.a.roll.back.was.done")
                }) getOrElse {
                closeWithCommit(connection)
                Right(affectedRows)
              })
      }) getOrElse {
        closeWithoutCommit(connection)
        Left("agnostic.delete.by.id.error.on.preparestatement")
      }
    }) getOrElse Left("agnostic.delete.by.id.error.no.db.connection")

  //
  // Delete by Id
  //
  def deleteById[I](
    connection: Connection,
    entityId: I,
    sqlDeleteById: String,
    filloutPsDelete: (PreparedStatement, I) => Either[String, PreparedStatement]): Either[String, Int] = {
    if (logger.isInfoEnabled) logger.info(s"deleteById|enter, about to prepare statement...")
    val prepStat = connection.prepareStatement(sqlDeleteById)
    if (logger.isInfoEnabled) logger.info(s"deleteById|statement ready")
    filloutPsDelete(prepStat, entityId)
    executeUpdate(prepStat) fold (
      error => {
        close(prepStat)
        logger.error(s"deleteById|$error")
        Left("agnostic.delete.by.id.error.on.execute.update")
      },
      affectedRows => {
        close(prepStat)
        Right(affectedRows)
      })
  }
}