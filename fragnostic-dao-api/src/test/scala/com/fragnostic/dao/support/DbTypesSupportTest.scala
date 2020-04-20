package com.fragnostic.dao.support

import java.time.LocalDateTime

import org.scalatest.{ FunSpec, Matchers }

class DbTypesSupportTest extends FunSpec with Matchers with DbTypesSupport {

  describe("Db Types Support Test") {

    it("Can Parse String to Timestamp") {

      val sqlTst = str2sqlTst("02-11-1967 12:54:54") fold (error => throw new IllegalStateException(error),
        sqlTst => sqlTst)

      val fechaHora: LocalDateTime = LocalDateTime.of(1967, 11, 2, 12, 54, 54)
      sqlTst.toLocalDateTime should be(fechaHora)

    }

    it("Can Not Parse Wrong String to Timestamp") {

      val error = str2sqlTst("8fff02-11-1967 12:54:54aaa") fold (error => error,
        sqlTst => sqlTst)

      error should be("str2sqltst.error")

    }

  }

}
