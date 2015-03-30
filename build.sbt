name := "spray-angular-learning"
scalaVersion in ThisBuild := "2.11.6"

lazy val ui = (project in file("ui")).enablePlugins(ScalaJSPlugin)
lazy val api = (project in file("api")).enablePlugins(SbtTwirl).dependsOn(ui).settings(
  (resources in Compile) ++= Seq(
    artifactPath.in(ui, Compile, fastOptJS).value,
    artifactPath.in(ui, Compile, fullOptJS).value,
    (classDirectory.in(ui, Compile).value / ".." / "ui-fastopt.js.map").get.head
  )
)
lazy val root = (project in file(".")).aggregate(api, ui)

//Revolver.reStart <<= Revolver.reStart in (api, Compile) dependsOn (fastOptJS in (ui, Compile))
