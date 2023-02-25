import sbt._

object Dependencies {

  lazy val fragnosticSupport          = "com.fragnostic"        % "fragnostic-support_2.13"        % "0.1.19-SNAPSHOT" % "test"
  lazy val fragnosticConfEnv          = "com.fragnostic"        %  "fragnostic-conf-env_2.13"      % "0.1.10-SNAPSHOT"

  lazy val logbackClassic             = "ch.qos.logback"        % "logback-classic"                % "1.3.0-alpha12" % "runtime"
  lazy val scalatestFunSpec           = "org.scalatest"         % "scalatest-funspec_2.13"         % "3.3.0-SNAP3" % Test
  lazy val mysql8JavaClient           = "mysql"                 % "mysql-connector-java"           % "8.0.27" % "test"

}
