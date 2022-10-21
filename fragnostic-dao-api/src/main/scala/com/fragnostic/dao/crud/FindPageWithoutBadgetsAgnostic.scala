package com.fragnostic.dao.crud

import com.fragnostic.dao.glue.PageWithoutLinks

import java.sql.{ Connection, PreparedStatement, ResultSet, SQLException }
import com.fragnostic.dao.support.{ ConnectionAgnostic, PageSupport, PreparedStatementParamsSupport, PreparedStatementSupport }
import org.slf4j.{ Logger, LoggerFactory }

trait FindPageWithoutBadgetsAgnostic extends ConnectionAgnostic with PreparedStatementSupport with PageSupport with PreparedStatementParamsSupport {

  private[this] val logger: Logger = LoggerFactory.getLogger("FindPageWithoutBadgetsAgnostic")

  def findPage[P](
    numPage: Int,
    orderBy: String,
    rowsPerPg: Int,
    optPrmsCount: Map[Int, (String, String)],
    optPrmsPage: Map[Int, (String, String)],
    sqlCountTotalRows: String,
    sqlFindPage: String,
    newRow: (ResultSet, Map[String, String]) => Either[String, P],
    args: Map[String, String] = Map.empty): Either[String, PageWithoutLinks[P]] =
    getConnection map (connection => {
      val eith =
        validate(connection, numPage, orderBy, rowsPerPg, optPrmsCount, optPrmsPage, sqlCountTotalRows, sqlFindPage, newRow, args)
      closeWithoutCommit(connection)
      eith
    }) getOrElse Left("agnostic.dao.find.page.error.db.nc")

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
    newRow: (ResultSet, Map[String, String]) => Either[String, P],
    args: Map[String, String]): Either[String, PageWithoutLinks[P]] =
    validate(connection, numPage, orderBy, rowsPerPg, prmsCount, prmsPage, sqlCountTotalRows, sqlFindPage, newRow, args)

  private def validate[P](
    connection: Connection,
    numPage: Int,
    orderBy: String,
    rowsPerPg: Int,
    prmsCount: Map[Int, (String, String)],
    prmsPage: Map[Int, (String, String)],
    sqlCountTotalRows: String,
    sqlFindPage: String,
    newRow: (ResultSet, Map[String, String]) => Either[String, P],
    args: Map[String, String]): Either[String, PageWithoutLinks[P]] = {

    if (numPage < 1) Left("find.page.agnostic.error.num.page.not.valid")
    else if (rowsPerPg < 1) Left("find.page.agnostic.error.rows.per.page.not.valid")
    else findPageCountTotalRows(connection, numPage, orderBy, rowsPerPg, prmsCount, prmsPage, sqlCountTotalRows, sqlFindPage, newRow, args)
  }

  private def findPageCountTotalRows[P](
    connection: Connection,
    numPage: Int,
    orderBy: String,
    rowsPerPg: Int,
    prmsCount: Map[Int, (String, String)],
    prmsPage: Map[Int, (String, String)],
    sqlCountTotalRows: String,
    sqlFindPage: String,
    newRow: (ResultSet, Map[String, String]) => Either[String, P],
    args: Map[String, String]): Either[String, PageWithoutLinks[P]] = {

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
              findPage(connection, numPage, orderBy, rowsPerPg, prmsPage, sqlFindPage, totalRows, newRow, args)
            } else {
              close(resultSet, prepStat)
              Right(PageWithoutLinks(0, "", 0, 0, 0, Nil, true): PageWithoutLinks[P])
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

  private def getRows[P](prepStat: PreparedStatement, resultSet: ResultSet, newRow: (ResultSet, Map[String, String]) => Either[String, P], args: Map[String, String]) =
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
    orderBy: String,
    rowsPerPg: Int,
    optPrmsPage: Map[Int, (String, String)],
    sqlFindPage: String,
    numRows: Int,
    newRow: (ResultSet, Map[String, String]) => Either[String, P],
    args: Map[String, String]): Either[String, PageWithoutLinks[P]] = {

    val numPages: Int = getNumPages(numRows, rowsPerPg)
    val numPage = getNumPage(numPageAparente, numPages)
    val idx = (numPage - 1) * rowsPerPg
    val prepStat = connection.prepareStatement(sqlFindPage)
    setParams(optPrmsPage, prepStat) fold (errors => {
      close(prepStat)
      Left(errors.mkString(", "))
    },
      col => {
        prepStat.setInt(col + 1, idx)
        prepStat.setInt(col + 2, rowsPerPg)
        executeQuery(prepStat) fold (error => {
          close(prepStat)
          logger.error(s"findPage | query executed with error: $error")
          Left(error)
        },
          resultSet =>
            getRows(prepStat, resultSet, newRow, args) fold (error => Left(error),
              list =>
                if (list.nonEmpty) {
                  Right(PageWithoutLinks(numPage, orderBy, rowsPerPg, numRows, numPages, list, list.isEmpty): PageWithoutLinks[P]) //
                } else {
                  Right(PageWithoutLinks(0, "", 0, 0, 0, Nil, true): PageWithoutLinks[P])
                } //
            ) //
        )

      })

  }

}
