package com.fragnostic.dao.support

import org.slf4j.{ Logger, LoggerFactory }

import scala.collection.mutable.ListBuffer

/**
 * Created by fernandobrule on 8/26/15.
 */
trait SqlOrderBySupport {

  private[this] val logger: Logger = LoggerFactory.getLogger("SqlOrderBySupport")

  def normalize(orderByMap: Map[String, String], rawOrderBy: String): String =
    if (orderByMap.contains(rawOrderBy.trim)) {
      rawOrderBy.trim
    } else {
      logger.warn(s"""normalize() - "order by map" does not contains key ${rawOrderBy.trim}""")
      ""
    }

  def applyOrderBy(
    rawSql: String,
    orderAvailable: Map[String, String],
    orderReq: String,
    orderDesc: Boolean //
  ): String = {

    val trimmed = normalize(orderAvailable, orderReq)
    if (trimmed == "") rawSql.replace("{{orderBy}}", "")
    else if (trimmed.indexOf(";") < 0) {
      if (orderAvailable.contains(trimmed))
        rawSql.replace(
          "{{orderBy}}",
          s"""order by ${orderAvailable(trimmed)}${desc(orderDesc)}""")
      else
        rawSql.replace("{{orderBy}}", "")
    } else {
      val orderByBuffer = new ListBuffer[String]
      trimmed.split(";") foreach (
        order =>
          if (orderAvailable.contains(order) && !orderByBuffer.contains(
            orderAvailable(order))) orderByBuffer += orderAvailable(order))

      if (orderByBuffer.nonEmpty)
        rawSql.replace(
          "{{orderBy}}",
          s""" order by ${
            orderByBuffer.mkString(
              ", ")
          } ${desc(orderDesc)} """)
      else
        rawSql.replace("{{orderBy}}", "")
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
