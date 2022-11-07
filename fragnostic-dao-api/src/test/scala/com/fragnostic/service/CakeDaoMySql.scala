package com.fragnostic.service

import com.fragnostic.service.impl.MySql8DataSource

object CakeDaoMySql {

  lazy val mysql8DataSourcePiece = new MySql8DataSource {}

  val mysql8DataSource = mysql8DataSourcePiece.dataSource

}
