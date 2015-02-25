name := "api"

fork in run := true

incOptions := incOptions.value.withNameHashing(true)
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

jacoco.settings
Revolver.settings
Revolver.enableDebugging()

val sprayVersion = "1.3.2"
val akkaVersion = "2.3.9"
val cascadeVersion = "0.4.1"
val jacksonVersion = "2.4.1"

libraryDependencies += "org.scaldi" %% "scaldi-akka" % "0.4"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"
libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.2.11"
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
libraryDependencies += "io.spray" %% "spray-routing" % sprayVersion
libraryDependencies += "io.spray" %% "spray-client" % sprayVersion
libraryDependencies += "com.paypal" %% "cascade-common" % cascadeVersion
libraryDependencies += "com.paypal" %% "cascade-http" % cascadeVersion
libraryDependencies += "com.paypal" %% "cascade-akka" % cascadeVersion
libraryDependencies += "com.paypal" %% "cascade-json" % cascadeVersion
libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % jacksonVersion
libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % jacksonVersion
libraryDependencies += "org.codehaus.woodstox" % "woodstox-core-asl" % "4.4.1"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.0.0-RC1"
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1200-jdbc41" exclude("org.slf4j", "slf4j-simple")
libraryDependencies += "com.typesafe.play" %% "anorm" % "2.4.0-M2"
libraryDependencies += "com.zaxxer" % "HikariCP-java6" % "2.3.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.3" % "test"
libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19" % "test"
libraryDependencies += "io.spray" %% "spray-testkit" % sprayVersion % "test"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-tests" % akkaVersion % "test"
