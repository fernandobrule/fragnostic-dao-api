package com.fragnostic.dao.support

import java.util.Properties

import com.fragnostic.conf.env.service.CakeConfEnvService
import com.fragnostic.support.FilesSupport
import com.mysql.cj.jdbc.MysqlDataSource
import org.scalatest.{ BeforeAndAfterAll }

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import org.slf4j.{ Logger, LoggerFactory }

import scala.language.postfixOps
import scala.sys.process._
import scala.util.{ Failure, Success, Try }

trait DaoLifeCycleSupport extends AnyFunSpec with Matchers with BeforeAndAfterAll with FilesSupport {

  private val MYSQL8_DATASOURCE_PROPERTY_FILE_NAME = "MYSQL8_DATASOURCE_PROPERTY_FILE_NAME"

  private[this] val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  private def getValues(props: Properties): Either[String, (String, Int, String, String, String)] =
    Try({
      val host: String = props.getProperty("dataSource.host")
      val port: Int = props.getProperty("dataSource.port").toInt
      val db: String = props.getProperty("dataSource.db")
      val usr: String = props.getProperty("dataSource.usr")
      val psw: String = props.getProperty("dataSource.psw")
      (host, port, db, usr, psw)
    }) match {
      case Success(tuple) =>
        Right(tuple)
      case Failure(exception) =>
        logger.error(s"getValues() - $exception")
        Left("dao.lyfecycle.support.get.values.error")
    }

  def getDataSource: Either[String, MysqlDataSource] =
    CakeConfEnvService.confEnvService
      .getString(key = MYSQL8_DATASOURCE_PROPERTY_FILE_NAME)
      .fold(
        error => {
          logger.error(s"getDataSource() - ERROR al cargar propertyFileName, $error")
          Left("dao.lyfecycle.support.get.datasource.on.get.conf")
        },
        opt =>
          opt map (propertyFileName =>
            loadProperties(propertyFileName) fold (error => {
              logger.error(s"getDataSource() - ERROR al leer archivo de propiedades, $error")
              Left("dao.lyfecycle.support.get.datasource.on.load.properties")
            },
              props =>
                getValues(props) fold (error => Left(error),
                  tuple => {
                    if (logger.isInfoEnabled()) logger.info(s"getDataSource() - $tuple")
                    val mysqlDataSource: MysqlDataSource = new MysqlDataSource()
                    mysqlDataSource.setServerName(tuple._1)
                    mysqlDataSource.setPort(tuple._2)
                    mysqlDataSource.setDatabaseName(tuple._3)
                    mysqlDataSource.setUser(tuple._4)
                    mysqlDataSource.setPassword(tuple._5)
                    Right(mysqlDataSource)
                  }))) getOrElse (Left("dao.lyfecycle.support.get.datasource.on.get.conf.property.file.name.does.not.exists")))

  val dataSource: MysqlDataSource = getDataSource fold (error => throw new IllegalStateException(error),
    dataSource => dataSource)

  override def beforeAll(): Unit = {
    val ans = "./fragnostic-dao-api/src/test/resources/beforeall/antbeforeall" !

    //
    println(s"ans:\n$ans\n")
    //
  }

  override def afterAll(): Unit = {
    //dataSource.close()
    val ans = "./fragnostic-dao-api/src/test/resources/afterall/antafterall" !

    //
    println(s"ans:\n$ans\n")
    //
  }

}
