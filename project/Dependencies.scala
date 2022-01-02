import sbt._
import Keys._

object Dependencies {

  lazy val fragnosticSupport          = "com.fragnostic"        % "fragnostic-support_2.13"        % "0.1.18-SNAPSHOT" % "test"
  lazy val fragnosticConfEnv          = "com.fragnostic"        %  "fragnostic-conf-env_2.13"      % "0.1.9-SNAPSHOT"

  lazy val logbackClassic             = "ch.qos.logback"        % "logback-classic"                % "1.3.0-alpha12" % "runtime"
  lazy val slf4jApi                   = "org.slf4j"             % "slf4j-api"                      % "2.0.0-alpha5"
  lazy val scalatestFunSpec           = "org.scalatest"        %% "scalatest-funspec"              % "3.3.0-SNAP3" % Test
  lazy val mysql8JavaClient           = "mysql"                 % "mysql-connector-java"           % "8.0.27" % "test"

}
