name := "spray-angular-learning"
scalaVersion in ThisBuild := "2.11.6"

lazy val ui = project in file("ui")
lazy val api = (project in file("api")).enablePlugins(SbtTwirl).dependsOn(ui)
lazy val root = (project in file(".")).aggregate(api, ui)