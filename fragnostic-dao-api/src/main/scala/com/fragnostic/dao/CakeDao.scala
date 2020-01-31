package com.fragnostic.dao

import com.fragnostic.dao.impl.{ HikariDataSourceImpl, MySql8DataSource }

object CakeDao {

  lazy val hikariDataSourcePiece = new HikariDataSourceImpl {}

  lazy val mysql8DataSourcePiece = new MySql8DataSource {}

  val mysql8DataSource = mysql8DataSourcePiece.dataSource

  val hikariDataSource = hikariDataSourcePiece.dataSource

}
