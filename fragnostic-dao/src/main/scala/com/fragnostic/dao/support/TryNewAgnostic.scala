package com.fragnostic.dao.support

import java.sql.ResultSet

import scala.util.Try

/**
 * Created by fernandobrule on 5/28/17.
 */
trait TryNewAgnostic {

  val defaultBigDecimal: BigDecimal = BigDecimal("0")

  def tryNewBigDecimal(resultSet: ResultSet, column: String, default: Option[BigDecimal] = None): BigDecimal =
    Try(resultSet.getBigDecimal(column)) fold (
      error => default map (value => value) getOrElse defaultBigDecimal.bigDecimal,
      available => available)

  def tryNewBigDecimal(resultSet: ResultSet, column: String): Option[BigDecimal] =
    Try(resultSet.getBigDecimal(column)) fold (
      error => None,
      available => Some(available))

  def tryNewString(resultSet: ResultSet, column: String, default: Option[String] = None): String =
    Try(resultSet.getString(column)) fold (
      error => default map (value => value) getOrElse "",
      available => available)

  def tryNewString(resultSet: ResultSet, column: String): Option[String] =
    Try(resultSet.getString(column)) fold (
      error => None,
      available => Some(available))

  def tryNewInt(resultSet: ResultSet, column: String, default: Option[Int] = None): Int =
    Try(resultSet.getInt(column)) fold (
      error => default map (value => value) getOrElse 0,
      available => available)

  def tryNewInt(resultSet: ResultSet, column: String): Option[Int] =
    Try(resultSet.getInt(column)) fold (
      error => None,
      available => Some(available))

}
