import sbt._
import Keys._

object Dependencies {

  lazy val fragnosticSupport = "com.fragnostic" % "fragnostic-support_2.13" % "0.1.17" % "test"
  lazy val fragnosticConfEnv = "com.fragnostic" % "fragnostic-conf-env_2.13" % "0.1.8" % "test"

  lazy val logbackClassic      = "ch.qos.logback" % "logback-classic"          % "1.2.3" % "provided"
  lazy val slf4jApi            = "org.slf4j"      % "slf4j-api"                % "1.7.25" % "provided"
  lazy val scalatest           = "org.scalatest" %% "scalatest"                % "3.2.2" % "test"

  lazy val mysql8JavaClient = "mysql" % "mysql-connector-java" % "8.0.18" % "test"

}
