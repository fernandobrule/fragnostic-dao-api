package com.fragnostic.dao.support

import java.sql.ResultSet

import org.slf4j.{ Logger, LoggerFactory }

/**
 * Created by fernandobrule on 9/17/16.
 */
trait RecursionSupport {

  private def logger: Logger = LoggerFactory.getLogger(getClass.getName)

  def newList[T](
    resultSet: ResultSet,
    newEntity: ResultSet => Either[String, T]): Either[String, List[T]] =
    if (resultSet.next()) {
      newEntity(resultSet) fold (
        error => {
          logger.error(s"newList() - $error")
          //newList(resultSet, newEntity)
          Left(error)
        },
        entity => newList(resultSet, newEntity) fold (
          error => Left(error),
          list => Right(entity :: list)))
    } else {
      Right(Nil)
    }

}
