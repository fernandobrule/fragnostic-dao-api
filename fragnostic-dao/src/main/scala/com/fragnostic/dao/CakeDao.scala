package com.fragnostic.dao

import com.fragnostic.dao.impl.DataSourceImpl

object CakeDao {

  lazy val dataSourcePiece = new DataSourceImpl {}

  val dataSource = dataSourcePiece.dataSource

}
