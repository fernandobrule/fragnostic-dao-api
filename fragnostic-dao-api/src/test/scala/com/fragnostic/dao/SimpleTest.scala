package com.fragnostic.dao

import com.fragnostic.dao.support.DaoLifeCycleSupport

class SimpleTest extends DaoLifeCycleSupport {

  describe("*** SimpleTest ***") {

    it("Can do Simple Test") {

      assertResult(true)(1 == 1)

    }
  }

}
