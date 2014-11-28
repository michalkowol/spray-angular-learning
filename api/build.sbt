name := "api"

fork in run := true

incOptions := incOptions.value.withNameHashing(true)
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

com.github.retronym.SbtOneJar.oneJarSettings
org.scalastyle.sbt.ScalastylePlugin.Settings
defaultScalariformSettings
coverallsSettings
Revolver.settings

libraryDependencies += "com.google.guava" % "guava" % "18.0"
libraryDependencies += "com.google.code.findbugs" % "jsr305" % "3.0.0"
libraryDependencies += "org.scaldi" %% "scaldi-akka" % "0.4"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.2" % "test"
libraryDependencies += "org.mockito" % "mockito-all" % "1.10.8" % "test"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"
libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11"

libraryDependencies += "com.typesafe.akka" %% "akka-actor-tests" % "2.3.7"
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.3.7"

val sprayVersion = "1.3.2"
libraryDependencies += "io.spray" %% "spray-routing" % sprayVersion
libraryDependencies += "io.spray" %% "spray-client" % sprayVersion
libraryDependencies += "io.spray" %% "spray-testkit" % sprayVersion % "test"
