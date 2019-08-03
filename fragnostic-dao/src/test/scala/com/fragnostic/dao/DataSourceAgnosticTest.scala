package com.fragnostic.dao

import java.sql.Connection

import org.scalatest._

/**
 * Created by fernandobrule on 8/19/16.
 */
class DataSourceAgnosticTest extends FunSpec with Matchers {

  describe("DataSource Agnostic Test") {

    it("Can Setup DataSource") {

      // https://mariadb.com/kb/en/library/about-mariadb-connector-j/
      // https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client/2.3.0

      CakeDao.dataSource.getDataSource().fold(
        error => throw new IllegalStateException(error),
        dataSource => {

          val connection: Connection = dataSource.getConnection
          connection.close()
          dataSource.close()

        })

    }

  }
}
