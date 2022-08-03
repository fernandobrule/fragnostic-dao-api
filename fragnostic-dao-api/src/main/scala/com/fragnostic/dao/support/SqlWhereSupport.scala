package com.fragnostic.dao.support

import scala.annotation.tailrec

trait SqlWhereSupport {

  private def buildAnd(tail: List[(String, String, String)], have: Boolean): String = {
    if (tail.isEmpty && !have) {
      ""
    } else if (tail.isEmpty && have) {
      "\n  and "
    } else if (have) {
      "\n  and "
    } else {
      ""
    }
  }

  private def buildValueDecorator(field: (String, String, String), whereReq: List[(String, String, String)], whereList: List[(String, String, String)]): String = {
    val whereTypeTuple: List[(String, String, String)] = whereList.filter(tuple => tuple._1 == field._1 && tuple._2 == field._2)
    if (whereTypeTuple.isEmpty) {
      s""" "${field._3}" """
    } else {
      if (whereTypeTuple.head._3 == "varchar") {
        s""" "${field._3}" """
      } else {
        s"${field._3}"
      }
    }
  }

  @tailrec
  private def buildWhereExpression(whereReq: List[(String, String, String)], sql: String, whereList: List[(String, String, String)], have: Boolean): String = {
    whereReq match {
      case head :: tail =>
        val field = head._1
        val operation = head._2
        val value = buildValueDecorator(head, whereReq, whereList)
        buildWhereExpression(tail, sql = s"$sql $field $operation $value ${buildAnd(tail, have)}", whereList, have)
      case Nil =>
        sql
    }
  }

  @tailrec
  private def whereByPredicate(field: String, whereBy: List[(String, String, String)]): Boolean = {
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

  private val pattern = "(where)".r
  private def haveWhere(sql: String): Boolean = {
    val all = pattern.findAllIn(sql)
    if (all.size > 1) {
      true
    } else {
      false
    }
  }

  def applyWhereBy(rawSql: String, whereReq: List[(String, String, String)], whereAvailable: List[(String, String, String)]): String = {
    val where: List[(String, String, String)] = whereAvailable.filter(tuple => whereByPredicate(tuple._1, whereReq))
    if (whereReq.isEmpty) {
      rawSql.replace("{{where}}", "")
    } else {
      val have: Boolean = haveWhere(rawSql)
      rawSql.replace("{{where}}", buildWhereExpression(whereReq, if (have) "" else "where", where, have))
    }
  }

}
