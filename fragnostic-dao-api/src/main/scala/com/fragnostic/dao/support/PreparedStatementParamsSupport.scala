package com.fragnostic.dao.support

import org.slf4j.{ Logger, LoggerFactory }

import java.sql.PreparedStatement

/**
 * Created by fernandobrule on 12/3/15.
 */
trait PreparedStatementParamsSupport extends StatementTypeHandler {

  private[this] val logger: Logger = LoggerFactory.getLogger("PreparedStatementParamsSupport")

  private val OK = "OK"

  def setParams(prmsCount: Map[Int, (String, String)], prepStat: PreparedStatement): Either[List[String], Int] = {
    //logger.info(s"setParams() - enter, prmsCount[${prmsCount}]")
    val errors = handleIterator(
      prmsCount.keysIterator,
      prmsCount,
      prepStat).filter(_ != OK)

    if (errors.isEmpty) {
      if (prmsCount.nonEmpty) {
        Right(prmsCount.keysIterator.max)
      } else {
        Right(0)
      }
    } else {
      Left(errors)
    }

  }

  private def handleNumParam(
    numParam: Int,
    prmsCount: Map[Int, (String, String)],
    prepStat: PreparedStatement): String = {
    //logger.info(s"handleNumParam() - enter, numParam[$numParam]")
    getHandler(prmsCount(numParam)._1) fold (
      error => {
        logger.error(s"handleNumParam() - ${error}")
        error
      },
      handler =>
        try {
          handler(prepStat, numParam, prmsCount)
          OK
        } catch {
          case e: Exception =>
            logger.error(s"handleNumParam() - ${e.getClass.getName} - ${e.getMessage}")
            s"$e"
          case e: Throwable =>
            logger.error(s"handleNumParam() - ${e.getClass.getName} - ${e.getMessage}")
            s"$e"
        } //
    )
  }

  private def handleIterator(
    iterator: Iterator[Int],
    prmsCount: Map[Int, (String, String)],
    prepStat: PreparedStatement): List[String] = {
    //logger.info(s"handleIterator() - enter")
    if (iterator.hasNext) {
      handleNumParam(iterator.next(), prmsCount, prepStat) :: handleIterator(
        iterator,
        prmsCount,
        prepStat)
    } else {
      Nil
    }
  }

}
