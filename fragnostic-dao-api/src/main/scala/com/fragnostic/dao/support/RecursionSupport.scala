package com.fragnostic.dao.support

import org.slf4j.{ Logger, LoggerFactory }

import java.sql.ResultSet

/**
 * Created by fernandobrule on 9/17/16.
 */
trait RecursionSupport {

  private[this] val logger: Logger = LoggerFactory.getLogger("RecursionSupport")

  // TODO java.lang.StackOverflowError
  def newList[T](resultSet: ResultSet, newEntity: (ResultSet, Map[String, String]) => Either[String, T], args: Map[String, String]): Either[String, List[T]] = {
    if (resultSet.next()) {
      newEntity(resultSet, args) fold (
        error => {
          logger.error(s"newList() - $error")
          Left(error)
        },
        entity => newList(resultSet, newEntity, args) fold (
          error => Left(error),
          list => Right(entity :: list) //
        ) //
      )
    } else {
      Right(Nil)
    }
  }

}
