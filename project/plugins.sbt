resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")
addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.6")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.7")
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.0.4")
