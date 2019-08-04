package com.fragnostic.dao

import com.fragnostic.dao.impl.HikariDataSourceImpl

object CakeDao {

  lazy val hikariDataSourcePiece = new HikariDataSourceImpl {}

  val hikariDataSource = hikariDataSourcePiece.dataSource

}
