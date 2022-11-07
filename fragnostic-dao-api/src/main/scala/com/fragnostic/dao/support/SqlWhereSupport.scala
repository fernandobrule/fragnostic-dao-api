package com.fragnostic.dao.support

import scala.annotation.tailrec

trait SqlWhereSupport {

  private def buildAnd(have: List[(String, String, String, String)]): String = {
    if (have.nonEmpty) "\n  and" else ""
  }

  private def buildValueDecorator(field: (String, String, String, String), whereList: List[(String, String, String, String)]): String = {
    val whereTypeTuple: List[(String, String, String, String)] = whereList.filter(tuple => tuple._1 == field._1 && tuple._2 == field._2)
    if (whereTypeTuple.isEmpty) {
      s""" "${field._3}" """
    } else {
      if (whereTypeTuple.head._3 == "varchar" || whereTypeTuple.head._3 == "date") {
        val right = {
          if (field._4.nonEmpty) {
            s""" and "${field._4}""""
          } else {
            ""
          }
        }
        s""""${field._3}"${right}"""
      } else {
        s"${field._3}"
      }
    }
  }

  @tailrec
  private def buildWhereExpression(whereReq: List[(String, String, String, String)], sql: String, whereList: List[(String, String, String, String)], have: Boolean): String = {
    whereReq match {
      case head :: tail =>
        val field = head._1
        val operation = head._2
        val value = buildValueDecorator(head, whereList)
        buildWhereExpression(tail, sql = s"$sql $field $operation $value${buildAnd(tail)}", whereList, have)
      case Nil =>
        sql
    }
  }

  @tailrec
  private def whereByPredicate(field: String, whereBy: List[(String, String, String, String)]): Boolean = {
    whereBy match {
      case head :: tail =>
        if (head._1 == field) {
          true
        } else {
          whereByPredicate(field, tail)
        }
      case Nil => false
    }
  }

  private val pattern = """(where)""".r
  private def haveWhere(sql: String): Boolean = {
    val all = pattern.findAllIn(sql)
    if (all.size > 1) {
      true
    } else {
      false
    }
  }

  // rawSql, mapWithRealColumns, whereAvailable
  def applyWhereBy(rawSql: String, mapWithRealColumns: List[(String, String, String, String)], whereAvailable: List[(String, String, String, String)]): String = {
    val where: List[(String, String, String, String)] = whereAvailable.filter(tuple => whereByPredicate(tuple._1, mapWithRealColumns))
    if (mapWithRealColumns.isEmpty) {
      rawSql.replace("{{where}}", "")
    } else {
      val have: Boolean = haveWhere(rawSql)
      rawSql.replace("{{where}}", buildWhereExpression(mapWithRealColumns, if (have) "and" else "where", where, have))
    }
  }

  def translate(mapNickToArgs: List[(String, String, String, String)], mapNickToRealColumns: Map[String, String]): List[(String, String, String, String)] = {
    val a = mapNickToArgs.filter(tuple => mapNickToRealColumns.contains(tuple._1))
    a.map(
      tuple => tuple.copy(_1 = mapNickToRealColumns(tuple._1)) //
    )
  }

}
