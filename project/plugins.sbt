scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

addSbtPlugin("org.xerial.sbt"       % "sbt-sonatype"         % "3.9.5")
addSbtPlugin("org.scalariform"      % "sbt-scalariform"      % "1.8.3")
addSbtPlugin("com.timushev.sbt"     % "sbt-updates"          % "0.5.1")
addSbtPlugin("com.jsuereth"         % "sbt-pgp"              % "2.0.2")
addSbtPlugin("com.eed3si9n"         % "sbt-unidoc"           % "0.4.3")

// https://github.com/sbt/sbt-jacoco
// https://www.scala-sbt.org/sbt-jacoco/getting-started.html
addSbtPlugin("com.github.sbt"       % "sbt-jacoco"           % "3.3.0")

// https://github.com/jrudolph/sbt-dependency-graph
addSbtPlugin("net.virtual-void"     % "sbt-dependency-graph" % "0.9.2")

