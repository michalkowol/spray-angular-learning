name := "spray-angular-learning"

version := "1.0"

scalaVersion in ThisBuild := "2.11.2"

lazy val api = project in file("api")

lazy val ui = project in file("ui")

lazy val root = (project in file(".")).aggregate(api, ui)