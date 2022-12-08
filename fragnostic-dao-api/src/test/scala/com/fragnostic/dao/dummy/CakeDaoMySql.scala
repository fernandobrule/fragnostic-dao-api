package com.fragnostic.dao.dummy

object CakeDaoMySql {

  lazy val mysql8DataSourcePiece = new MySql8DataSource {}

  val mysql8DataSource = mysql8DataSourcePiece.dataSource

}
