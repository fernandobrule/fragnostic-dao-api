package com.fragnostic.dao.crud

import com.fragnostic.conf.env.service.CakeConfEnvService
import com.fragnostic.dao.dummy.DaoLifeCycleSupport
import com.fragnostic.dao.dummy.glue.Dummy
import com.fragnostic.dao.glue.Page

import java.sql.ResultSet
import scala.util.{ Failure, Success, Try }

class FindPageAgnosticTest extends DaoLifeCycleSupport with FindPageAgnostic {

  describe("*** FindPageAgnosticTest ***") {

    it("Can Get Page") {

      val dbSchema = CakeConfEnvService.confEnvService.getString("DATASOURCE_DB") fold (
        error => throw new IllegalStateException(error),
        dbSchema => dbSchema //
      )

      val numPage: Int = 1
      val numMaxBadgets: Short = 5
      val rowsPerPg: Int = 5
      val orderDescFlag: Boolean = true
      val orderCriterion: String = "texto" // texto -> dummy1_field2
      val orderAvailable: Map[String, String] = Map( //"texto" -> "dummy1_field2" //
      )

      val mapNickToArgs: List[(String, String, String, String)] = List( //
      )

      val mapNickToRealColumns: List[(String, String, String, String)] = List( //
      )

      val prmsCount: Map[Int, (String, String)] = Map(1 -> ("String", "four"))
      val prmsPage: Map[Int, (String, String)] = Map(1 -> ("String", "four"))

      val sqlCountTotalRows: String =
        s"""
          | select count(*) as total_rows
          | from $dbSchema.dummy1
          | where dummy1_field3 = ?
          |
          |""".stripMargin

      val sqlFindPage: String =
        s"""
          |
          | select
          |   dummy1_id,
          |   dummy1_field1,
          |   dummy1_field2,
          |   dummy1_field3
          | from
          |   $dbSchema.dummy1
          | where
          |   dummy1_field3 = ?
          | order by
          |   dummy1_field2
          | limit ?, ?
          |
          |""".stripMargin

      def newRow(rs: ResultSet, args: Map[String, String]): Either[String, Dummy] = {
        Try(
          Dummy( //
            rs.getLong("dummy1_id"),
            rs.getString("dummy1_field1"),
            rs.getString("dummy1_field2"),
            rs.getString("dummy1_field3") //
          ) //
        ) match {
            case Success(dummy) => Right(dummy)
            case Failure(exception) => Left(exception.getMessage)
          }
      }

      val args: Map[String, String] = Map.empty

      val page: Page[Dummy] = findPage(
        numPage,
        numMaxBadgets,
        rowsPerPg,
        orderDescFlag,
        orderCriterion,
        orderAvailable,
        mapNickToArgs,
        mapNickToRealColumns,
        prmsCount,
        prmsPage,
        sqlCountTotalRows,
        sqlFindPage,
        newRow,
        args //
      ) fold (
        error => throw new IllegalStateException(error),
        page => page //
      )

      assertResult(1)(page.numPage)
      assertResult(true)(page.list.nonEmpty)

      page.list.foreach(println(_))

    }

  }

}
