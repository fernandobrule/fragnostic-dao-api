package com.fragnostic.dao.support

import scala.collection.mutable.ListBuffer

/**
 * Created by fernandobrule on 8/26/15.
 */
trait SqlOrderBySupport {

  def applyOrderBy(
    orderByMap: Map[String, String],
    rawSql: String,
    rawOrderBy: String,
    rawDesc: Boolean): String = {

    val trimmed = rawOrderBy.trim
    if (trimmed == "") rawSql.replace("{{orderBy}}", "")
    else if (trimmed.indexOf(";") < 0) {
      if (orderByMap.contains(trimmed))
        rawSql.replace(
          "{{orderBy}}",
          s""" order by ${orderByMap(trimmed)} ${desc(rawDesc)}""")
      else
        rawSql.replace("{{orderBy}}", "")
    } else {
      val orderByBuffer = new ListBuffer[String]
      trimmed.split(";") foreach (
        order =>
          if (orderByMap.contains(order) && !orderByBuffer.contains(
            orderByMap(order))) orderByBuffer += orderByMap(order))

      if (orderByBuffer.nonEmpty)
        rawSql.replace(
          "{{orderBy}}",
          s""" order by ${
            orderByBuffer.mkString(
              ", ")
          } ${desc(rawDesc)} """)
      else
        rawSql.replace("{{orderBy}}", "")
    }

  }

  def desc(rawDesc: Boolean): String = if (rawDesc) " desc " else ""

}
