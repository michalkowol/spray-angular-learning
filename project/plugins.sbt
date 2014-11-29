resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"
resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.4.0")
addSbtPlugin("org.scala-sbt.plugins" % "sbt-onejar" % "0.8")
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.1")
addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "0.99.0")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.0.3")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.6")
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")