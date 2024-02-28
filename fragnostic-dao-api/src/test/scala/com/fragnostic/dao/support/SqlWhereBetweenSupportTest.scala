package com.fragnostic.dao.support

import org.scalatest.funspec.AnyFunSpec

class SqlWhereBetweenSupportTest extends AnyFunSpec with SqlWhereSupport {

  describe("*** SqlWhereBetweenSupportTest ***") {

    it("Can Build Where Between") {

      val rawSql: String = "{{where}}"

      val mapNickToRealColumns: Map[String, String] = Map(
        "date" -> "installment_date",
        "concept" -> "concept_id",
        "installmentStatus" -> "installment_status_type_id" //
      )

      val mapNickToArgs: List[(String, String, String, String)] = List( //
        ("date", "between", "2022-05-01", "2022-05-30") //
      )

      val whereAvailable: List[(String, String, String, String)] = List(
        ("concept_id", "=", "bigint", ""),
        ("installment_status_type_id", "=", "varchar", ""),
        ("installment_date", "between", "date", "") //
      )

      val mapWithRealColumns: List[(String, String, String, String)] = translate(mapNickToArgs, mapNickToRealColumns)

      val expected: String = """where installment_date between "2022-05-01" and "2022-05-30""""
      val where: String = applyWhereBy(rawSql, mapWithRealColumns, whereAvailable)

      assertResult(expected)(where)

    }

  }

}
