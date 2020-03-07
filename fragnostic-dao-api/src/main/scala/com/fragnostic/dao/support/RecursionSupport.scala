package com.fragnostic.dao.support

import java.sql.ResultSet

import org.slf4j.{ Logger, LoggerFactory }

/**
 * Created by fernandobrule on 9/17/16.
 */
trait RecursionSupport {

  private[this] val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  def newList[T](
    resultSet: ResultSet,
    newEntity: (ResultSet, Seq[String]) => Either[String, T],
    args: Seq[String]): Either[String, List[T]] =
    if (resultSet.next()) {
      newEntity(resultSet, args) fold (
        error => {
          logger.error(s"newList() - $error")
          Left(error)
        },
        entity => newList(resultSet, newEntity, args) fold (
          error => Left(error),
          list => Right(entity :: list)))
    } else {
      Right(Nil)
    }

}
