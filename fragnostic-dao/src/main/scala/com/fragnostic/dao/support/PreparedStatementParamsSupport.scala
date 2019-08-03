package com.fragnostic.dao.support

import java.sql.{ PreparedStatement, SQLException }

import org.slf4j.LoggerFactory

/**
 * Created by fernandobrule on 12/3/15.
 */
trait PreparedStatementParamsSupport extends StatementTypeHandler {

  private def logger = LoggerFactory.getLogger(getClass.getName)

  private val OK = "OK"

  def setParams(
    params: Map[Int, (String, String)],
    prepStat: PreparedStatement): Either[List[String], Int] = {
    if (logger.isInfoEnabled) logger.info(s"setParams | enter")

    if (logger.isInfoEnabled) logger.info(s"setParams | params:$params")
    val errors = handleIterator(
      params.keysIterator,
      params,
      prepStat).filter(_ != OK)
    if (errors.isEmpty) {
      if (params.nonEmpty) {
        Right(params.keysIterator.max)
      } else {
        Right(0)
      }
    } else {
      Left(errors)
    }

  }

  private def handleNumParam(
    numParam: Int,
    params: Map[Int, (String, String)],
    prepStat: PreparedStatement): String = {
    if (logger.isInfoEnabled) logger.info(s"handleNumParam | enter, numParam:$numParam")
    handle(params(numParam)._1) fold (
      error => {
        logger.error(s"handleNumParam | $error")
        error
      },
      handler =>
        try {
          if (logger.isInfoEnabled) logger.info(s"handleNumParam | have handler, numParam:$numParam")
          handler(prepStat, numParam, params)
          OK
        } catch {
          case e: SQLException => {
            logger.error(s"handleNumParam | $e")
            s"$e"
          }
          case e: Exception => {
            logger.error(s"handleNumParam | $e")
            s"$e"
          }
          case e: Throwable => {
            logger.error(s"handleNumParam | $e")
            s"$e"
          }
        })
  }

  private def handleIterator(
    iterator: Iterator[Int],
    params: Map[Int, (String, String)],
    prepStat: PreparedStatement): List[String] = {
    if (logger.isInfoEnabled) logger.info(s"handleIterator | enter")
    if (iterator.hasNext) {
      if (logger.isInfoEnabled) logger.info(s"handleIterator | have next")
      handleNumParam(iterator.next(), params, prepStat) :: handleIterator(
        iterator,
        params,
        prepStat)
    } else {
      Nil
    }
  }

}
