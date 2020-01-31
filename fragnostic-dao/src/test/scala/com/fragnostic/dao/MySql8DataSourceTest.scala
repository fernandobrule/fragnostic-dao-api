package com.fragnostic.dao

import java.sql.Connection

import org.scalatest.{ FunSpec, Matchers }

class MySql8DataSourceTest extends FunSpec with Matchers {

  describe("MySql8 DataSource Test") {

    it("Can Get MySql8 DataSource") {
      // https://mariadb.com/kb/en/library/about-mariadb-connector-j/
      // https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client/2.3.0
      CakeDao.mysql8DataSource.getDataSource fold (
        error => throw new IllegalStateException(error),
        dataSource => {
          val connection: Connection = dataSource.getConnection
          connection.close()
          // TODO dataSource.close()
        })
    }

  }

}
