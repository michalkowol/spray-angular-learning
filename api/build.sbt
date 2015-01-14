name := "api"

fork in run := true

incOptions := incOptions.value.withNameHashing(true)
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

com.github.retronym.SbtOneJar.oneJarSettings
org.scalastyle.sbt.ScalastylePlugin.Settings
defaultScalariformSettings
jacoco.settings
Revolver.settings

val sprayVersion = "1.3.2"
val akkaVersion = "2.3.8"

libraryDependencies += "com.google.guava" % "guava" % "18.0"
libraryDependencies += "com.google.code.findbugs" % "jsr305" % "3.0.0"
libraryDependencies += "org.scaldi" %% "scaldi-akka" % "0.4"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"
libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11"
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
libraryDependencies += "io.spray" %% "spray-routing" % sprayVersion
libraryDependencies += "io.spray" %% "spray-client" % sprayVersion

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.3" % "test"
libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19" % "test"
libraryDependencies += "io.spray" %% "spray-testkit" % sprayVersion % "test"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-tests" % akkaVersion

