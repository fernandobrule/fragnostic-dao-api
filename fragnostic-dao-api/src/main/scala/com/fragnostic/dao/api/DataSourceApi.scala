package com.fragnostic.dao.api

import javax.sql.DataSource

trait DataSourceApi {

  def dataSource: DataSourceApi

  trait DataSourceApi {

    def getDataSource: Either[String, DataSource]

  }

}
