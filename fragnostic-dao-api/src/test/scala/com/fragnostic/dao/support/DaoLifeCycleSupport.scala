package com.fragnostic.dao.support

import com.fragnostic.service.CakeDaoMySql
import com.fragnostic.support.FilesSupport
import com.mysql.cj.jdbc.MysqlDataSource
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funspec.AnyFunSpec
import org.slf4j.{ Logger, LoggerFactory }

import scala.language.postfixOps
import scala.sys.process._

trait DaoLifeCycleSupport extends AnyFunSpec with BeforeAndAfterAll with FilesSupport {

  private[this] val logger: Logger = LoggerFactory.getLogger("DaoLifeCycleSupport")

  val dataSource: MysqlDataSource = CakeDaoMySql.mysql8DataSource.getDataSource fold (
    error => {
      logger.error(s"On get DataSource: $error")
      throw new IllegalStateException(error)
    },
    dataSource => dataSource //
  )

  override def beforeAll(): Unit = {
    logger.info(s"beforeAll() - enter")

    val ans = "./fragnostic-dao-api/src/test/resources/beforeall/antbeforeall" !

    //
    println(s"ans:\n$ans\n")
    //
  }

  override def afterAll(): Unit = {
    logger.info(s"afterAll() - enter")

    val ans = "./fragnostic-dao-api/src/test/resources/afterall/antafterall" !

    //
    println(s"ans:\n$ans\n")
    //
  }

}
