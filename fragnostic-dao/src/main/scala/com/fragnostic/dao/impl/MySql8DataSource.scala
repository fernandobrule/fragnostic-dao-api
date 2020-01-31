package com.fragnostic.dao.impl

import java.util.Properties

import com.fragnostic.conf.service.CakeServiceConf
import com.fragnostic.dao.api.DataSourceApi
import com.fragnostic.support.FilesSupport
import com.mysql.cj.jdbc.MysqlDataSource
import org.slf4j.{ Logger, LoggerFactory }

import scala.util.Try

trait MySql8DataSource extends DataSourceApi {

  def dataSource = new DefaultDataSource

  class DefaultDataSource extends DataSourceApi with FilesSupport {

    private[this] val logger: Logger = LoggerFactory.getLogger(getClass.getName)

    private val MYSQL8_DATASOURCE_PROPERTY_FILE_NAME = "MYSQL8_DATASOURCE_PROPERTY_FILE_NAME"

    //
    // https://www.programcreek.com/java-api-examples/index.php?api=com.mysql.cj.jdbc.MysqlDataSource
    // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
    // https://www.journaldev.com/2509/java-datasource-jdbc-datasource-example
    // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html
    //
    private def getValues(props: Properties): Either[String, (String, Int, String, String, String)] =
      Try({
        val host: String = props.getProperty("dataSource.host")
        val port: Int = props.getProperty("dataSource.port").toInt
        val db: String = props.getProperty("dataSource.db")
        val usr: String = props.getProperty("dataSource.usr")
        val psw: String = props.getProperty("dataSource.psw")
        Right((host, port, db, usr, psw))
      }) getOrElse Left("mysql8.data.source.get.props.error")

    override def getDataSource: Either[String, MysqlDataSource] =
      CakeServiceConf.confService.getConf(MYSQL8_DATASOURCE_PROPERTY_FILE_NAME).fold(
        error => {
          logger.error(s"getDataSource() - ERROR al cargar propertyFileName, $error")
          Left("mysql8.datasource.impl.get.datasource.on.get.conf")
        },
        propertyFileName =>
          loadProperties(propertyFileName) fold (
            error => {
              logger.error(s"getDataSource() - ERROR al leer archivo de propiedades, $error")
              Left("mysql8.datasource.impl.get.datasource.on.load.properties")
            },
            props => getValues(props) fold (
              error => Left(error),
              tuple => {
                val mysqlDataSource: MysqlDataSource = new MysqlDataSource()
                mysqlDataSource.setServerName(tuple._1)
                mysqlDataSource.setPort(tuple._2)
                mysqlDataSource.setDatabaseName(tuple._3)
                mysqlDataSource.setUser(tuple._4)
                mysqlDataSource.setPassword(tuple._5)
                Right(mysqlDataSource)
              })))

  }

}
