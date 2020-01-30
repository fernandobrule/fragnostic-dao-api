package com.fragnostic.dao.impl

import com.fragnostic.conf.service.CakeServiceConf
import com.fragnostic.dao.api.DataSourceApi
import com.fragnostic.support.FilesSupport
import com.mysql.cj.jdbc.MysqlDataSource
import org.slf4j.{ Logger, LoggerFactory }

trait MySql8DataSource extends DataSourceApi {

  def dataSource = new DefaultDataSource

  class DefaultDataSource extends DataSourceApi with FilesSupport {

    private[this] val logger: Logger = LoggerFactory.getLogger(getClass.getName)

    private val MYSQL8_DATASOURCE_PROPERTY_FILE_NAME = "MYSQL8_DATASOURCE_PROPERTY_FILE_NAME"

    override def getDataSource: Either[String, MysqlDataSource] =
      CakeServiceConf.confService.getConf(MYSQL8_DATASOURCE_PROPERTY_FILE_NAME).fold(
        error => {
          logger.error(s"getDataSource() - ERROR al cargar propertyFileName, $error")
          Left("mysql8.datasource.impl.get.datasource.on.get.conf")
        },
        propertyFileName => {

          if (logger.isInfoEnabled) logger.info(s"getDataSource() - propertyFileName : $propertyFileName")

          loadProperties(propertyFileName) fold (
            error => {
              logger.error(s"getDataSource() - ERROR al leer archivo de propiedades, $error")
              Left("mysql8.datasource.impl.get.datasource.on.load.properties")
            },
            props => {

              //
              // https://www.programcreek.com/java-api-examples/index.php?api=com.mysql.cj.jdbc.MysqlDataSource
              // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
              // https://www.journaldev.com/2509/java-datasource-jdbc-datasource-example
              // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html
              

              //val url: String = props.getProperty("dataSource.url")
              val host: String = props.getProperty("dataSource.host")
              val port: Int = props.getProperty("dataSource.port").toInt
              val db: String = props.getProperty("dataSource.db")
              val usr: String = props.getProperty("dataSource.usr")
              val psw: String = props.getProperty("dataSource.psw")
              //if (logger.isInfoEnabled) logger.info(s"getDataSource() - url[$url], usr[$usr], psw[$psw]")

              val mysqlDataSource: MysqlDataSource = new MysqlDataSource()
              //mysqlDataSource.setURL(url)
              mysqlDataSource.setServerName(host)
              mysqlDataSource.setPort(port)
              mysqlDataSource.setDatabaseName(db)
              mysqlDataSource.setUser(usr)
              mysqlDataSource.setPassword(psw)

              Right(mysqlDataSource)

            })
        })
  }

}
