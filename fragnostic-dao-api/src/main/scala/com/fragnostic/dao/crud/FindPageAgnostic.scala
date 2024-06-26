package com.fragnostic.dao.crud

import com.fragnostic.dao.glue.Page
import com.fragnostic.dao.support._
import org.slf4j.{ Logger, LoggerFactory }

import java.sql.{ Connection, PreparedStatement, ResultSet, SQLException }

/**
 * Created by Fernando Brule on 30-06-2015 22:23:00.
 * Generated by Tesseract.
 */
trait FindPageAgnostic extends ConnectionAgnostic with PreparedStatementSupport with PageSupport with PreparedStatementParamsSupport with SqlOrderBySupport with SqlWhereSupport {

  private[this] val logger: Logger = LoggerFactory.getLogger("FindPageAgnostic")

  def findPage[P](
    numPage: Int,
    numMaxBadgets: Short,
    rowsPerPg: Int,
    orderDescFlag: Boolean,
    orderCriterion: String,
    orderAvailable: Map[String, String],
    mapNickToArgs: List[(String, String, String, String)],
    mapNickToRealColumns: List[(String, String, String, String)],
    prmsCount: Map[Int, (String, String)],
    prmsPage: Map[Int, (String, String)],
    sqlCountTotalRows: String, // to get total rows
    sqlFindPage: String, // to get a page with all his columns
    newRow: (ResultSet, Map[String, String]) => Either[String, P], // P new instance
    args: Map[String, String] = Map.empty // args for P new instance
  ): Either[String, Page[P]] = {
    getConnection map (connection => {
      val eith = validate(connection, numPage, numMaxBadgets, rowsPerPg, orderDescFlag, orderCriterion, orderAvailable, mapNickToArgs, mapNickToRealColumns, prmsCount, prmsPage, sqlCountTotalRows, sqlFindPage, newRow, args)
      closeWithoutCommit(connection)
      eith
    }) getOrElse Left("agnostic.dao.find.page.error.db.nc")
  }

  def findPage[P](
    connection: Connection,
    numPage: Int,
    numMaxBadgets: Short,
    rowsPerPg: Int,
    orderDesc: Boolean,
    orderReq: String,
    orderAvailable: Map[String, String],
    mapNickToArgs: List[(String, String, String, String)],
    mapNickToRealColumns: List[(String, String, String, String)],
    prmsCount: Map[Int, (String, String)],
    prmsPage: Map[Int, (String, String)],
    sqlCountTotalRows: String,
    sqlFindPage: String,
    newRow: (ResultSet, Map[String, String]) => Either[String, P],
    args: Map[String, String]): Either[String, Page[P]] = {
    validate(connection, numPage, numMaxBadgets, rowsPerPg, orderDesc, orderReq, orderAvailable, mapNickToArgs, mapNickToRealColumns, prmsCount, prmsPage, sqlCountTotalRows, sqlFindPage, newRow, args)
  }

  private def validate[P](
    connection: Connection,
    numPage: Int,
    numMaxBadgets: Short,
    rowsPerPg: Int,
    orderDescFlag: Boolean,
    orderCriterion: String,
    mapNickToArgs: Map[String, String],
    mapNickToRealColumns: List[(String, String, String, String)],
    whereAvailable: List[(String, String, String, String)],
    prmsCount: Map[Int, (String, String)],
    prmsPage: Map[Int, (String, String)],
    sqlCountTotalRows: String,
    sqlFindPage: String,
    newRow: (ResultSet, Map[String, String]) => Either[String, P],
    args: Map[String, String]): Either[String, Page[P]] = {
    if (numPage < 1) {
      Left("find.page.agnostic.error.num.page.not.valid")
    } else if (numMaxBadgets < 1) {
      Left("find.page.agnostic.error.num.max.badgets.not.valid")
    } else if (!sqlFindPage.contains("limit")) {
      Left("find.page.agnostic.error.sql.find.page.does.not.contains.limit.clause")
    } else if (rowsPerPg < 1) {
      Left("find.page.agnostic.error.rows.per.page.not.valid")
    } else {

      val sqlFindPageAfterApplyOrderBy: String = applyOrderBy(sqlFindPage, mapNickToArgs, orderCriterion, orderDescFlag)
      if (logger.isInfoEnabled) {
        logger.debug(s"validate() - sqlFindPageAfterApplyOrderBy:\n$sqlFindPageAfterApplyOrderBy\n=======================")
      }

      val sqlFindPageAfterApplyWhere: String = applyWhereBy(sqlFindPageAfterApplyOrderBy, mapNickToRealColumns, whereAvailable)
      if (logger.isInfoEnabled) {
        logger.debug(s"validate() - sqlFindPageAfterApplyWhere:\n$sqlFindPageAfterApplyWhere\n=======================")
      }

      val sqlCountTotalRowsAfterApplyWhere: String = applyWhereBy(sqlCountTotalRows, mapNickToRealColumns, whereAvailable)
      if (logger.isInfoEnabled) {
        logger.debug(s"validate() - sqlCountTotalRowsAfterApplyWhere:\n$sqlCountTotalRowsAfterApplyWhere\n=======================")
      }

      findPageCountTotalRows(connection, numPage, numMaxBadgets, orderCriterion, rowsPerPg, prmsCount, prmsPage, sqlCountTotalRowsAfterApplyWhere, sqlFindPageAfterApplyWhere, newRow, args)
    }
  }

  private def findPageCountTotalRows[P](
    connection: Connection,
    numPage: Int,
    numMaxBadgets: Short,
    orderBy: String,
    rowsPerPg: Int,
    prmsCount: Map[Int, (String, String)],
    prmsPage: Map[Int, (String, String)],
    sqlCountTotalRows: String,
    sqlFindPage: String,
    newRow: (ResultSet, Map[String, String]) => Either[String, P],
    args: Map[String, String]): Either[String, Page[P]] = {

    val prepStat = connection.prepareStatement(sqlCountTotalRows)
    setParams(prmsCount, prepStat) fold (errors => Left(errors.mkString(",")),
      col => {
        executeQuery(prepStat) fold (error => {
          close(prepStat)
          logger.error(s"findPageCountTotalRows | query executed with error: $error \n\tquery: $sqlCountTotalRows \n\t$prmsCount")
          Left(error)
        },
          resultSet => {
            if (resultSet.next()) {
              val totalRows = resultSet.getInt("total_rows")
              close(resultSet, prepStat)
              findPage(connection, numPage, numMaxBadgets, orderBy, rowsPerPg, prmsPage, sqlFindPage, totalRows, newRow, args)
            } else {
              close(resultSet, prepStat)
              Right(Page(0, "", 0, 0, Nil, 0, 0, 0, Nil, listIsEmpty = true): Page[P])
            }
          })
      })
  }

  private def addRow[P](resultSet: ResultSet, newRow: (ResultSet, Map[String, String]) => Either[String, P], args: Map[String, String]): List[P] =
    if (resultSet.next())
      newRow(resultSet, args) fold (error => {
        logger.error(s"addRow() - $error")
        addRow(resultSet, newRow, args)
      },
        row => row :: addRow(resultSet, newRow, args))
    else {
      Nil
    }

  private def getRows[P](prepStat: PreparedStatement, resultSet: ResultSet, newRow: (ResultSet, Map[String, String]) => Either[String, P], args: Map[String, String]): Either[String, List[P]] =
    try {
      val rows = addRow(resultSet, newRow, args)
      close(resultSet, prepStat)
      Right(rows)
    } catch {
      case e: SQLException =>
        close(resultSet, prepStat)
        logger.error(s"getRows | oooops, $e")
        Left("agnostic.dao.find.page.error.adding.row.1")
      case e: Exception =>
        close(resultSet, prepStat)
        logger.error(s"getRows | oooops, $e")
        Left("agnostic.dao.find.page.error.adding.row.2")
    }

  private def findPage[P](
    connection: Connection,
    numPageAparente: Int,
    numMaxBadgets: Short,
    orderBy: String,
    rowsPerPg: Int,
    optPrmsPage: Map[Int, (String, String)],
    sqlFindPage: String,
    numRows: Int,
    newRow: (ResultSet, Map[String, String]) => Either[String, P],
    args: Map[String, String]): Either[String, Page[P]] = {

    val numPages: Int = getNumPages(numRows, rowsPerPg)
    val numPage = getNumPage(numPageAparente, numPages)
    val idx = (numPage - 1) * rowsPerPg
    val prepStat = connection.prepareStatement(sqlFindPage)

    setParams(optPrmsPage, prepStat) fold ( //
      errors => {
        close(prepStat)
        Left(errors.mkString(", "))
      },
      col => {

        prepStat.setInt(col + 1, idx)
        prepStat.setInt(col + 2, rowsPerPg)

        executeQuery(prepStat) fold (
          error => {
            close(prepStat)
            logger.error(s"findPage() - query executed with error: $error\n$sqlFindPage\n---------------------\n")
            Left(error)
          },
          resultSet => {
            getRows(prepStat, resultSet, newRow, args) fold ( //
              error => Left(error),
              list => if (list.nonEmpty) {
                val linksLimits = getPageLinks(numPage, numPages, numMaxBadgets)
                Right(Page(numPage, orderBy, linksLimits._1, linksLimits._2, linksLimits._3, rowsPerPg, numRows, numPages, list, list.isEmpty): Page[P])
              } else {
                Right(Page(0, "", 0, 0, Nil, 0, 0, 0, Nil, listIsEmpty = true): Page[P])
              } //
            )
          } //
        )

      } //
    )

  }

}
