name := "spray-angular-learning"

version := "1.0"

scalaVersion := "2.11.2"

incOptions := incOptions.value.withNameHashing(true)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

Revolver.settings

Seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

org.scalastyle.sbt.ScalastylePlugin.Settings

instrumentSettings

scalariformSettings

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)

unmanagedResourceDirectories in Compile += baseDirectory.value / "src/main/webapp/dist"

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "17.0",
  "com.google.code.findbugs" % "jsr305" % "3.0.0",
  "org.scaldi" %% "scaldi-akka" % "0.4",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "org.scalatest" %% "scalatest" % "2.2.0" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "org.json4s" %% "json4s-native" % "3.2.10"
)

libraryDependencies ++= {
  val sprayVersion = "1.3.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor-tests" % "2.3.4",
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-client" % sprayVersion,
    "io.spray" %% "spray-testkit" % sprayVersion % "test"
  )
}