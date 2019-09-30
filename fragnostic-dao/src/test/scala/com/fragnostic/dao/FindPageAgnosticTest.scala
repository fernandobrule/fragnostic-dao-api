package com.fragnostic.dao

import java.sql.ResultSet

import com.fragnostic.dao.glue.CodeName
import com.fragnostic.dao.support.{ DaoLifeCycleSupport, SqlOrderBySupport }

class FindPageAgnosticTest extends DaoLifeCycleSupport with FindPageAgnostic with SqlOrderBySupport {

  describe("Find Page Agnostic Test") {

    it("Can Retrieve Page") {

      val numPage: Int = 11

      val nummaxBadgets: Short = 5

      val rowsPerPg: Int = 10

      val optPrmsCount: Map[Int, (String, String)] = Map[Int, (String, String)]()

      val optPrmsPage: Map[Int, (String, String)] = Map[Int, (String, String)]()

      val sqlCountTotalRows: String =
        s"""
           | select count(*) as total_rows
           | from db_admp.test_table
           |
     """.stripMargin

      val sqlFindPageStage: String =
        s"""
           | select test_id,
           |        test_code,
           |        test_name
           | from db_admp.test_table
           | {{orderBy}}
           | limit ?, ?
           |
     """.stripMargin

      val orderByMap: Map[String, String] =
        Map(
          "code" -> "test_code",
          "name" -> "test_name")

      val orderBy = "name"
      val orderDesc = false
      val sqlFindPage: String = applyOrderBy(orderByMap, sqlFindPageStage, orderBy, orderDesc)

      def newCodeName = (rs: ResultSet) => try {
        Right(CodeName(
          rs.getString("test_code"),
          rs.getString("test_name")))
      } catch {
        case e: Exception => Left(e.getMessage)
      }

      val tuple = findPage(numPage, nummaxBadgets, orderBy, rowsPerPg, optPrmsCount, optPrmsPage, sqlCountTotalRows, sqlFindPage, newCodeName) fold (
        error => throw new IllegalStateException(error),
        tuple => tuple)

      println(s"numPage: Long = ${tuple._1}")
      println(s"orderBy: String = ${tuple._2}")
      println(s"linksLimitLeft: Long = ${tuple._3}")
      println(s"linksLimitRight: Long = ${tuple._4}")
      println(s"linksAsList: List[Int] = ${tuple._5}")
      println(s"rowsPerPage: Long = ${tuple._6}")
      println(s"numRows: Long = ${tuple._7}")
      println(s"numPages: Long = ${tuple._8}")
      tuple._9 foreach (cn => println(cn))
      println(s"isEmpty: Boolean = ${tuple._10}")

    }

  }

}
