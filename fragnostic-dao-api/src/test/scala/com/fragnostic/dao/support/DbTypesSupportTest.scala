package com.fragnostic.dao.support

import org.scalatest.funspec.AnyFunSpec

import java.time.LocalDateTime

class DbTypesSupportTest extends AnyFunSpec with DbTypesSupport {

  describe("Db Types Support Test") {

    it("Can Parse String to Timestamp") {

      val sqlTst = str2sqlTst("02-11-1967 12:54:54") fold (
        error => throw new IllegalStateException(error),
        sqlTst => sqlTst //
      )

      val fechaHora: LocalDateTime = LocalDateTime.of(1967, 11, 2, 12, 54, 54)

      assertResult(sqlTst.toLocalDateTime)(fechaHora)

    }

    it("Can Not Parse Wrong String to Timestamp") {

      val error = str2sqlTst("8fff02-11-1967 12:54:54aaa") fold (
        error => error,
        sqlTst => sqlTst //
      )

      assertResult(error)("str2sqltst.error")

    }

  }

}
