package com.fragnostic.dao.dummy

import com.fragnostic.conf.env.service.CakeConfEnvService
import com.fragnostic.dao.api.DataSourceApi
import com.mysql.cj.jdbc.MysqlDataSource

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
    private def getConfig(
      host: String,
      port: String,
      db: String,
      usr: String,
      psw: String //
    ): Either[String, (String, Int, String, String, String)] =
      for {
        host <- CakeConfEnvService.confEnvService.getString(host)
        port <- CakeConfEnvService.confEnvService.getInt(port)
        db <- CakeConfEnvService.confEnvService.getString(db)
        usr <- CakeConfEnvService.confEnvService.getString(usr)
        psw <- CakeConfEnvService.confEnvService.getString(psw)
      } yield {
        (host, port, db, usr, psw)
      }

    override def getDataSource(
      host: String,
      port: String,
      db: String,
      usr: String,
      psw: String //
    ): Either[String, MysqlDataSource] = {
      getConfig(host, port, db, usr, psw) fold (
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
