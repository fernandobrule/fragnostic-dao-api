package com.fragnostic.dao.support

import org.slf4j.{ Logger, LoggerFactory }

import java.sql.ResultSet
import scala.annotation.tailrec

/**
 * Created by fernandobrule on 9/17/16.
 */
trait RecursionSupport {

  private[this] val logger: Logger = LoggerFactory.getLogger("RecursionSupport")

  final def newList[T](resultSet: ResultSet, newEntity: (ResultSet, Map[String, String]) => Either[String, T], args: Map[String, String]): List[T] = {
    @tailrec
    def newList[P](resultSet: ResultSet, newEntity: (ResultSet, Map[String, String]) => Either[String, P], args: Map[String, String], list: List[P]): List[P] = {
      if (resultSet.next()) {
        val list2 = newEntity(resultSet, args) fold (
          error => {
            logger.error(s"newList() - $error")
            list
          },
          entity => entity :: list //
        )
        newList(resultSet, newEntity, args, list2)
      } else {
        list.reverse
      }
    }

    newList(resultSet, newEntity, args, List[T]())
  }

}
