name := "ui"

unmanagedResourceDirectories in Compile += baseDirectory.value / "webapp" / "dist"

lazy val compileUI = taskKey[Int]("Build UI")
compileUI := {
  def throwErrorIfFailure(exitVal: Int, errorMessage: String): Unit = if (exitVal > 0) sys.error(errorMessage)
  def runOrFail(cmd: String): Unit = {
    throwErrorIfFailure(Process(cmd, baseDirectory.value / "webapp").!, s"$cmd failed")
  }
  //runOrFail("npm install")
  //runOrFail("bower install")
  //runOrFail("gulp")
  0
}

compile <<= (compile in Compile) dependsOn compileUI
