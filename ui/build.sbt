name := "ui"

exportJars := true
unmanagedResourceDirectories in Compile += baseDirectory.value / "webapp" / "dist"