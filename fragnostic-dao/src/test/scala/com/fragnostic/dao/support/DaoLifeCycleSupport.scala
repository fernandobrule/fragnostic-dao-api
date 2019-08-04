package com.fragnostic.dao.support

import com.fragnostic.dao.CakeDao
import com.zaxxer.hikari.HikariDataSource
import org.scalatest.{ BeforeAndAfterAll, FunSpec, Matchers }
import org.slf4j.{ Logger, LoggerFactory }

trait DaoLifeCycleSupport extends FunSpec with Matchers with BeforeAndAfterAll {

  private def logger: Logger = LoggerFactory.getLogger(getClass)

  val dataSource: HikariDataSource = CakeDao.hikariDataSource.getDataSource() fold (
    error => throw new IllegalStateException(error),
    dataSource => dataSource)

  override def afterAll(): Unit = {
    dataSource.close()
    if (logger.isInfoEnabled) logger.info(s"afterAll() - DataSource has been closed")
  }

}
