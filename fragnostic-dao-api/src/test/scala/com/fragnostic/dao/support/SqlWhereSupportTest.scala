package com.fragnostic.dao.support

import org.scalatest.funspec.AnyFunSpec

class SqlWhereSupportTest extends AnyFunSpec with SqlWhereSupport {

  describe("*** SqlWhereSupportTest ***") {

    it("Can Build Where") {

      val rawSql: String =
        """
          |where
          |  installment_debt_id = debt_id
          |  and installment_installment_status_type_id = installment_status_type_id
          |  and debt_currency_type_code = currency_type_code
          |  and debt_concept_id = concept_id
          |  {{where}}
          |
          |  """".stripMargin

      val mapNickToRealColumns: Map[String, String] = Map(
        "date" -> "installment_date",
        "concept" -> "concept_id",
        "installmentStatus" -> "installment_status_type_id" //
      )

      val mapNickToArgs: List[(String, String, String, String)] = List( //
        ("concept", "=", "123", ""),
        ("idonotexist", "=", "1", ""),
        ("installmentStatus", "=", "PAID", "") //
      )

      val whereAvailable: List[(String, String, String, String)] = List(
        ("concept_id", "=", "bigint", ""),
        ("installment_status_type_id", "=", "varchar", ""),
        ("installment_date", "between", "date", "") //
      )

      //      val mapWithRealColumns: List[(String, String, String, String)] = translate(mapNickToArgs, mapNickToRealColumns)
      val mapWithRealColumns: List[(String, String, String, String)] = translate(mapNickToArgs, mapNickToRealColumns)

      val expected: String =
        """
          |where
          |  installment_debt_id = debt_id
          |  and installment_installment_status_type_id = installment_status_type_id
          |  and debt_currency_type_code = currency_type_code
          |  and debt_concept_id = concept_id
          |  and concept_id = 123
          |  and installment_status_type_id = "PAID"
          |
          |  """".stripMargin

      val where: String = applyWhereBy(rawSql, mapWithRealColumns, whereAvailable)

      assertResult(expected)(where)

    }

  }

}
