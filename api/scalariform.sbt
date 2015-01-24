import scalariform.formatter.preferences._

defaultScalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignParameters, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(FormatXml, false)
  .setPreference(PreserveSpaceBeforeArguments, true)
