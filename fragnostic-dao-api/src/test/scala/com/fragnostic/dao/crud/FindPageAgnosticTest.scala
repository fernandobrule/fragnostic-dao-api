package com.fragnostic.dao.crud

import com.fragnostic.dao.glue.Page
import com.fragnostic.dao.support.DaoLifeCycleSupport
import com.fragnostic.glue.Customer

import java.sql.ResultSet
import scala.util.Try

class FindPageAgnosticTest extends DaoLifeCycleSupport with FindPageAgnostic {

  describe("*** FindPageAgnosticTest ***") {

    it("Can Get Page") {

      val numPage: Int = 1
      val numMaxBadgets: Short = 5
      val rowsPerPg: Int = 5
      val orderDescFlag: Boolean = true
      val orderCriterion: String = "yep"
      val orderAvailable: Map[String, String] = Map.empty
      val mapNickToArgs: List[(String, String, String, String)] = Nil
      val mapNickToRealColumns: List[(String, String, String, String)] = Nil
      val prmsCount: Map[Int, (String, String)] = Map.empty
      val prmsPage: Map[Int, (String, String)] = Map.empty
      val sqlCountTotalRows: String = "sql Count Total Rows"
      val sqlFindPage: String = "sql Find Page"
      def newRow(rs: ResultSet, args: Map[String, String]): Either[String, Customer] = {
        Try(
          Right(Customer(rs.getString("asdas"), rs.getString("qwewq"))) //
        ) getOrElse (Left("ooooops"))
      }

      val args: Map[String, String] = Map.empty

      val page: Page[Customer] = findPage(numPage, numMaxBadgets, rowsPerPg, orderDescFlag, orderCriterion, orderAvailable, mapNickToArgs, mapNickToRealColumns, prmsCount, prmsPage, sqlCountTotalRows, sqlFindPage, newRow, args) fold (
        error => throw new IllegalStateException(error),
        page => page //
      )

      assertResult(1)(page.numPage)
      assertResult(true)(page.list.nonEmpty)

    }

  }

}
