package com.fragnostic.dao.crud

import java.sql.Connection

import com.fragnostic.dao.support.{ CloseResourceAgnostic, ConnectionAgnostic, StatementAgnostic }

import scala.util.{ Failure, Success, Try }

/**
 * Created by fernandobrule on 4/18/17.
 */
trait UpdateSimpleAgnostic extends CloseResourceAgnostic with ConnectionAgnostic with StatementAgnostic {

  def update(
    sqlUpdate: String,
    validateAffRows: Option[Int] = Some(1),
    commit: Boolean): Either[String, Int] =
    getConnection map (connection =>
      update(connection, sqlUpdate) match {
        case Success(affectedRows) => {

          validateAffRows map (
            number =>
              if (affectedRows == number) {
                evaCommit(connection, commit)
                Right(affectedRows)
              } else {
                closeWithoutCommit(connection)
                Left(s"upd.agnostic.error.aff.rows.are.not.equal.to.expected.a.roll.back.was.done")
              }) getOrElse {
              evaCommit(connection, commit)
              Right(affectedRows)
            }

        }
        case Failure(exception) => {
          closeWithoutCommit(connection)
          Left(exception.getMessage)
        }
      }) getOrElse Left("update.simple.agnostic.error.no.db.conn")

  private def update(connection: Connection, sqlUpdate: String): Try[Int] =
    for {
      prepStat <- Try(connection.prepareStatement(sqlUpdate))
      affectedRows <- Try(prepStat.executeUpdate())
    } yield {
      close(prepStat)
      affectedRows
    }

  private def evaCommit(connection: Connection, commit: Boolean) =
    if (commit) closeWithCommit(connection)
    else closeWithoutCommit(connection)

}
