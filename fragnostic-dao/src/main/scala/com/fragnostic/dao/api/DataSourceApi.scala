package com.fragnostic.dao.api

import com.zaxxer.hikari.HikariDataSource

trait DataSourceApi {

  def dataSource: DataSourceApi

  trait DataSourceApi {

    def getDataSource(): Either[String, HikariDataSource]

  }

}
