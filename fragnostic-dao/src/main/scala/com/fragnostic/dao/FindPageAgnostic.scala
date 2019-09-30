package com.fragnostic.dao

import java.sql.{ Connection, PreparedStatement, ResultSet, SQLException }

import com.fragnostic.dao.support._
import org.slf4j.{ Logger, LoggerFactory }

/**
 * Created by Fernando Brule on 30-06-2015 22:23:00.
 * Generated by Tesseract.
 */
trait FindPageAgnostic extends ConnectionAgnostic with PreparedStatementSupport with PageSupport with PreparedStatementParamsSupport {

  private def logger: Logger = LoggerFactory.getLogger(getClass.getName)

  def findPage[P](
    connection: Connection,
    numPage: Int,
    nummaxBadgets: Short,
    orderBy: String,
    rowsPerPg: Int,
    prmsCount: Map[Int, (String, String)],
    prmsPage: Map[Int, (String, String)],
    sqlCountTotalRows: String,
    sqlFindPage: String,
    newRow: ResultSet => Either[String, P]): Either[String, (Long, String, Long, Long, List[Int], Long, Long, Long, List[P], Boolean)] =
    findPageCountTotalRows(
      connection,
      numPage,
      nummaxBadgets,
      orderBy,
      rowsPerPg,
      prmsCount,
      prmsPage,
      sqlCountTotalRows,
      sqlFindPage,
      newRow)

  def findPage[P](
    numPage: Int,
    nummaxBadgets: Short,
    orderBy: String,
    rowsPerPg: Int,
    optPrmsCount: Map[Int, (String, String)],
    optPrmsPage: Map[Int, (String, String)],
    sqlCountTotalRows: String,
    sqlFindPage: String,
    newRow: ResultSet => Either[String, P]): Either[String, (Long, String, Long, Long, List[Int], Long, Long, Long, List[P], Boolean)] =
    getConnection map (
      connection => {
        if (logger.isInfoEnabled) logger.info(s"findPage enter")
        val eith = findPageCountTotalRows(
          connection,
          numPage,
          nummaxBadgets,
          orderBy,
          rowsPerPg,
          optPrmsCount,
          optPrmsPage,
          sqlCountTotalRows,
          sqlFindPage,
          newRow)
        closeWithoutCommit(connection)
        eith
      }) getOrElse Left("agnostic.dao.find.page.error.db.nc")

  private def findPageCountTotalRows[P](
    connection: Connection,
    numPage: Int,
    nummaxBadgets: Short,
    orderBy: String,
    rowsPerPg: Int,
    prmsCount: Map[Int, (String, String)],
    prmsPage: Map[Int, (String, String)],
    sqlCountTotalRows: String,
    sqlFindPage: String,
    newRow: ResultSet => Either[String, P]): Either[String, (Long, String, Long, Long, List[Int], Long, Long, Long, List[P], Boolean)] = {

    val prepStat = connection.prepareStatement(sqlCountTotalRows)
    setParams(prmsCount, prepStat) fold (
      errors => Left(errors.mkString(",")),
      col => {
        if (logger.isInfoEnabled) logger.info(
          s"findPageCountTotalRows | parameters are setted..., about to execute query, col: $col")
        executeQuery(prepStat) fold (
          error => {
            close(prepStat)
            logger.error(
              s"findPageCountTotalRows | query executed with error: $error \n\tquery: $sqlCountTotalRows \n\t$prmsCount")
            Left(error)
          },
          resultSet => {
            if (logger.isInfoEnabled) logger.info(s"findPageCountTotalRows | query executed 1")
            if (resultSet.next()) {
              if (logger.isInfoEnabled) logger.info(s"findPageCountTotalRows | next")
              val totalRows = resultSet.getInt("total_rows")
              if (logger.isInfoEnabled) logger.info(s"findPageCountTotalRows | totalRows: $totalRows")
              close(resultSet, prepStat)
              findPage(
                connection,
                numPage,
                nummaxBadgets,
                orderBy,
                rowsPerPg,
                //optPrmsCount,
                prmsPage,
                sqlFindPage,
                newRow,
                totalRows)
            } else {
              if (logger.isInfoEnabled) logger.info(
                s"findPageCountTotalRows | totalRows: 0, empty set...")
              close(resultSet, prepStat)
              Right((0, "", 0, 0, Nil, 0, 0, 0, Nil, true): (Long, String, Long, Long, List[Int], Long, Long, Long, List[P], Boolean))
            }
          })
      })
  }

  private def addRow[P](resultSet: ResultSet, newRow: ResultSet => Either[String, P]): List[P] =
    if (resultSet.next())
      newRow(resultSet) fold (
        error => {
          logger.error(s"addRow() - $error")
          addRow(resultSet, newRow)
        },
        row => row :: addRow(resultSet, newRow))
    else {
      Nil
    }

  private def getRows[P](prepStat: PreparedStatement, resultSet: ResultSet, newRow: ResultSet => Either[String, P]) =
    try {
      val rows = addRow(resultSet, newRow)
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
    nummaxBadgets: Short,
    orderBy: String,
    rowsPerPg: Int,
    //optPrmsCount: Map[Int, (String, String)],
    optPrmsPage: Map[Int, (String, String)],
    sqlFindPage: String,
    newRow: ResultSet => Either[String, P],
    numRows: Int): Either[String, (Long, String, Long, Long, List[Int], Long, Long, Long, List[P], Boolean)] = {

    val numPages: Int = getNumPages(numRows, rowsPerPg)
    val numPage = getNumPage(numPageAparente, numPages)
    val idx = (numPage - 1) * rowsPerPg
    if (logger.isInfoEnabled) logger.info(
      s"findPage | numPage: $numPage de numPages: $numPages, interval: $idx ~ $rowsPerPg")
    val prepStat = connection.prepareStatement(sqlFindPage)
    setParams(optPrmsPage, prepStat) fold (
      errors => {
        close(prepStat)
        Left(errors.mkString(", "))
      },
      col => {
        prepStat.setInt(col + 1, idx)
        prepStat.setInt(col + 2, rowsPerPg)
        if (logger.isInfoEnabled) logger.info(
          s"findPage | parameters are setted..., limit 1:$idx, limit 2:$rowsPerPg, about to execute query")
        executeQuery(prepStat) fold (
          error => {
            close(prepStat)
            logger.error(s"findPage | query executed with error: $error")
            Left(error)
          },
          resultSet => {

            if (logger.isInfoEnabled) logger.info(s"findPage | query executed 2")

            getRows(prepStat, resultSet, newRow) fold (
              error => Left(error),
              list =>
                if (list.nonEmpty) {
                  val linksLimits = getPageLinks(numPage, numPages, nummaxBadgets)
                  Right((
                    numPage,
                    orderBy,
                    linksLimits._1,
                    linksLimits._2,
                    linksLimits._3,
                    rowsPerPg,
                    numRows,
                    numPages,
                    list,
                    list.isEmpty): (Long, String, Long, Long, List[Int], Long, Long, Long, List[P], Boolean))
                } else Right((0, "", 0, 0, Nil, 0, 0, 0, Nil, true): (Long, String, Long, Long, List[Int], Long, Long, Long, List[P], Boolean)))
          })

      })

  }

}
