name := "spray-angular-learning"
version in ThisBuild := "1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.11.4"

lazy val ui = project in file("ui")
lazy val api = (project in file("api")).enablePlugins(SbtTwirl).dependsOn(ui)
lazy val root = (project in file(".")).aggregate(api, ui)