package com.fragnostic.dao.impl

import java.sql.SQLNonTransientConnectionException
import java.util.Properties

import com.fragnostic.conf.service.CakeServiceConf
import com.fragnostic.dao.api.DataSourceApi
import com.fragnostic.support.FilesSupport
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException
import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }
import org.slf4j.{ Logger, LoggerFactory }

trait DataSourceImpl extends DataSourceApi {

  def dataSource = new DefaultDataSource

  class DefaultDataSource extends DataSourceApi with FilesSupport {

    private def logger: Logger = LoggerFactory.getLogger(getClass.getName)

    private val regex = """\{\{(\w+)\}\}""".r

    private def isEnvar(value: String): Option[String] =
      value match {
        case regex(envar) => Some(envar)
        case _ => None
      }

    override def getDataSource(): Either[String, HikariDataSource] =
      CakeServiceConf.confService.getConf("HIKARI_DATASOURCE_PROPERTY_FILE_NAME").fold(
        error => {
          logger.error(s"getDataSource() - ERROR al cargar propertyFileName, $error")
          Left("fragnostic.dao.error.on.get.template")
        },
        propertyFileName => {

          if (logger.isInfoEnabled) logger.info(s"getDataSource() - propertyFileName : $propertyFileName")

          loadProperties(propertyFileName) fold (
            error => {
              logger.error(s"getDataSource() - ERROR al leer archivo de propiedades, $error")
              Left("fragnostic.dao.error.on.load.properties")
            },
            props => {

              val propNamesEnum = props.propertyNames()
              while (propNamesEnum.hasMoreElements) {

                val key = propNamesEnum.nextElement().toString
                val value = props.getProperty(key)
                isEnvar(value) map (
                  envar => CakeServiceConf.confService.getConf(envar) fold (
                    error => throw new IllegalStateException(s"fragnostic.dao.error.envar.do.not.exists.$envar"),
                    realValue => {
                      if (logger.isInfoEnabled) logger.info(s"getDataSoutce() - $envar => $realValue")
                      props.put(key, realValue)
                    }))

              }

              try {
                //dataSourceClassName
                //dataSource.user
                //dataSource.password
                //dataSource.databaseName
                //dataSource.portNumber
                //dataSource.serverName
                val config = new HikariConfig(props)
                Right(new HikariDataSource(config))
              } catch {
                case e: PoolInitializationException =>
                  logger.error("getDataSource() - PoolInitializationException, {}", e.getMessage)
                  Left("fragnostic.dao.error.on.get.datasource")
                case e: SQLNonTransientConnectionException =>
                  logger.error("getDataSource() - SQLNonTransientConnectionException, {}", e.getMessage)
                  Left("fragnostic.dao.error.on.get.datasource")
                case e: Throwable =>
                  logger.error("getDataSource() - Throwable, {}", e.getMessage)
                  Left("fragnostic.dao.error.on.get.datasource")
              }

            })

        })

  }

}

