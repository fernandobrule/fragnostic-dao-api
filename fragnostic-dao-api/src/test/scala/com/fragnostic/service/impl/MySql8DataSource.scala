package com.fragnostic.service.impl

import com.fragnostic.dao.api.DataSourceApi
import com.mysql.cj.jdbc.MysqlDataSource
import com.fragnostic.conf.env.service.CakeConfEnvService.confEnvService.{ getInt, getString }

trait MySql8DataSource extends DataSourceApi {

  def dataSource = new DefaultDataSource

  class DefaultDataSource extends DataSourceApi {

    //
    // https://www.programcreek.com/java-api-examples/index.php?api=com.mysql.cj.jdbc.MysqlDataSource
    // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
    // https://www.journaldev.com/2509/java-datasource-jdbc-datasource-example
    // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html
    // https://tersesystems.com/blog/2012/12/27/error-handling-in-scala/
    //
    private val getConfig: Either[String, (String, Int, String, String, String)] =
      for {
        host <- getString("DATASOURCE_HOST")
        port <- getInt("DATASOURCE_PORT")
        db <- getString("DATASOURCE_DB")
        usr <- getString("DATASOURCE_USR")
        psw <- getString("DATASOURCE_PSW")
      } yield {
        (host, port, db, usr, psw)
      }

    override def getDataSource: Either[String, MysqlDataSource] = {
      getConfig fold (
        error => Left(error),
        config => {
          val mysqlDataSource: MysqlDataSource = new MysqlDataSource()
          mysqlDataSource.setServerName(config._1)
          mysqlDataSource.setPort(config._2)
          mysqlDataSource.setDatabaseName(config._3)
          mysqlDataSource.setUser(config._4)
          mysqlDataSource.setPassword(config._5)
          Right(mysqlDataSource)
        } //
      )
    }

  }

}
