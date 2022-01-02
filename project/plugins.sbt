scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

addSbtPlugin("com.github.sbt"       % "sbt-jacoco"           % "3.4.0")
addSbtPlugin("com.jsuereth"         % "sbt-pgp"              % "2.1.1")
addSbtPlugin("com.timushev.sbt"     % "sbt-updates"          % "0.5.2")
addSbtPlugin("net.virtual-void"     % "sbt-dependency-graph" % "0.10.0-RC1")
addSbtPlugin("org.scalariform"      % "sbt-scalariform"      % "1.8.3")
addSbtPlugin("org.xerial.sbt"       % "sbt-sonatype"         % "3.9.7")
