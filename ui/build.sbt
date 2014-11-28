name := "ui"

exportJars := true
unmanagedResourceDirectories in Compile += baseDirectory.value / "webapp" / "dist"

lazy val compileUI = taskKey[Unit]("Build UI")
compileUI := {
  def throwErrorIfFailure(exitVal: Int, message: String): Unit = if (exitVal > 0) sys.error(message)
  throwErrorIfFailure(Process("ls", baseDirectory.value / "webapp").!, "npm start failed")
}

compile <<= (compile in Compile) dependsOn compileUI