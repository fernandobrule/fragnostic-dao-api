package com.fragnostic.dao.support

import org.scalatest.funspec.AnyFunSpec

class SqlWhereSupportTest extends AnyFunSpec with SqlWhereSupport {

  describe("*** SqlWhereSupportTest ***") {

    it("Can Build Where Between") {

      val rawSql: String = "{{where}}"

      val whereReqMap: Map[String, String] = Map(
        "date" -> "installment_date",
        "concept" -> "concept_id",
        "installmentStatus" -> "installment_status_type_id" //
      )

      val whereReq: List[(String, String, String, String)] = List( //
        ("date", "between", "2022-05-01", "2022-05-30") //
      )

      val whereAvailable: List[(String, String, String, String)] = List(
        ("concept_id", "=", "bigint", ""),
        ("installment_status_type_id", "=", "varchar", ""),
        ("installment_date", "between", "date", "") //
      )

      val whereReqTranslated: List[(String, String, String, String)] = translate(whereReq, whereReqMap)

      val expected: String = """where installment_date between "2022-05-01" and "2022-05-30""""
      val where: String = applyWhereBy(rawSql, whereReqTranslated, whereAvailable)

      assertResult(expected)(where)

    }

  }

}
