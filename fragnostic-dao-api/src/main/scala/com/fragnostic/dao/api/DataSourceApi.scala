package com.fragnostic.dao.api

import javax.sql.DataSource

trait DataSourceApi {

  def dataSource: DataSourceApi

  trait DataSourceApi {

    def getDataSource(
      host: String,
      port: String,
      db: String,
      usr: String,
      psw: String //
    ): Either[String, DataSource]

  }

}
