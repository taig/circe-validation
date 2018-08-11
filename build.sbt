lazy val root = project
  .in(file("."))
  .enablePlugins(BuildInfoPlugin, TutPlugin)
  .settings(Settings.common)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](
      "normalizedName" -> (normalizedName in circeValidationJVM).value,
      organization,
      version),
    buildInfoObject := "Build",
    buildInfoPackage := "io.taig.circe.validation",
    publish := {},
    publishArtifact := false,
    publishLocal := {},
    libraryDependencies ++=
      "io.circe" %% "circe-generic" % "0.9.3" % "tut" ::
        "io.circe" %% "circe-parser" % "0.9.3" % "tut" ::
        Nil,
    tutSourceDirectory := baseDirectory.value / "tut",
    tutTargetDirectory := baseDirectory.value
  )
  .dependsOn(circeValidationJVM)
  .aggregate(circeValidationJVM, circeValidationJS)

lazy val circeValidation = crossProject
  .in(file("."))
  .settings(Settings.common)
  .settings(
    libraryDependencies ++=
      "io.circe" %%% "circe-core" % "0.9.3" ::
        "io.circe" %%% "circe-generic" % "0.9.3" % "test" ::
        "org.scalatest" %%% "scalatest" % "3.0.5" % "test" ::
        Nil,
    name := "circe-validation"
  )

lazy val circeValidationJVM = circeValidation.jvm

lazy val circeValidationJS = circeValidation.js

addCommandAlias("scalafmtAll", ";scalafmt;test:scalafmt;scalafmtSbt")
addCommandAlias("scalafmtTestAll",
                ";scalafmtCheck;test:scalafmtCheck;scalafmtSbtCheck")
