name := "spray-angular-learning"
scalaVersion in ThisBuild := "2.11.6"

lazy val ui = project in file("ui")
lazy val js = (project in file("js")).enablePlugins(ScalaJSPlugin)
lazy val api = (project in file("api")).enablePlugins(SbtTwirl).dependsOn(ui).settings(
  (resources in Compile) ++= Seq(
    (fastOptJS in (js, Compile)).value.data,
    (fullOptJS in (js, Compile)).value.data,
//    (artifactPath in(js, Compile, packageJSDependencies)).value,
//    (artifactPath in(js, Compile, packageScalaJSLauncher)).value,
    ((classDirectory in (js, Compile)).value / ".." / "js-fastopt.js.map").get.head
  )
)
lazy val root = (project in file(".")).aggregate(api, ui, js)

Revolver.reStart <<= Revolver.reStart in (api, Compile) dependsOn (fastOptJS in (js, Compile))
