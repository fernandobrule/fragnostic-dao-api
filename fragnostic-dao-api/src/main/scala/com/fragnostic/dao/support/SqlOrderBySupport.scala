package com.fragnostic.dao.support

import org.slf4j.{ Logger, LoggerFactory }

import scala.collection.mutable.ListBuffer

/**
 * Created by fernandobrule on 8/26/15.
 */
trait SqlOrderBySupport {

  private[this] val logger: Logger = LoggerFactory.getLogger("SqlOrderBySupport")

  def normalize(mapNickToArgs: Map[String, String], orderCriterion: String): String =
    if (orderCriterion.trim.isEmpty) {
      logger.warn(s"normalize() - orderCriterion is empty")
      ""
    } else if (mapNickToArgs.contains(orderCriterion.trim)) {
      orderCriterion.trim
    } else {
      logger.warn(s"normalize() - mapNickToArgs[${mapNickToArgs.mkString}] does not contains orderCriterion[${orderCriterion.trim}]")
      ""
    }

  def applyOrderBy( //sqlFindPage, mapNickToArgs, orderCriterion, orderDescFlag
    sqlFindPage: String,
    mapNickToArgs: Map[String, String],
    orderCriterion: String,
    orderDescFlag: Boolean //
  ): String = {

    val trimmed = normalize(mapNickToArgs, orderCriterion)
    if (trimmed == "") sqlFindPage.replace("{{orderBy}}", "")
    else if (trimmed.indexOf(";") < 0) {
      if (mapNickToArgs.contains(trimmed))
        sqlFindPage.replace(
          "{{orderBy}}",
          s"""order by ${mapNickToArgs(trimmed)}${desc(orderDescFlag)}""" //
        )
      else
        sqlFindPage.replace("{{orderBy}}", "")
    } else {
      val orderByBuffer = new ListBuffer[String]
      trimmed.split(";") foreach (
        order =>
          if (mapNickToArgs.contains(order) && !orderByBuffer.contains(mapNickToArgs(order))) {
            orderByBuffer += mapNickToArgs(order)
          } //
      )

      if (orderByBuffer.nonEmpty) {
        sqlFindPage.replace(
          "{{orderBy}}",
          s""" order by ${
            orderByBuffer.mkString(
              ", ")
          } ${desc(orderDescFlag)} """ //
        )
      } else {
        sqlFindPage.replace("{{orderBy}}", "")
      }
    }

  }

  def desc(rawDesc: Boolean): String = {
    if (rawDesc) {
      " desc "
    } else {
      ""
    }
  }

}
