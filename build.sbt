lazy val root = project
  .in(file("."))
  .enablePlugins(BuildInfoPlugin, MdocPlugin)
  .settings(Settings.common)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](
      "normalizedName" -> (normalizedName in circeValidationJVM).value,
      organization,
      version
    ),
    buildInfoObject := "Build",
    buildInfoPackage := "io.taig.circe.validation",
    publish := {},
    publishArtifact := false,
    publishLocal := {},
    libraryDependencies ++=
      "io.circe" %% "circe-generic" % "0.12.2" % Compile ::
        "io.circe" %% "circe-parser" % "0.12.2" % Compile ::
        Nil,
    mdocIn := baseDirectory.value / "mdoc",
    mdocOut := baseDirectory.value,
    commands ++= Seq(compileWithMacroParadise),
    addCommandAlias("compile", "compileWithMacroParadise")
  )
  .dependsOn(circeValidationJVM)
  .aggregate(circeValidationJVM, circeValidationJS)

lazy val circeValidation = crossProject
  .in(file("."))
  .settings(Settings.common)
  .settings(
    libraryDependencies ++=
      "io.circe" %%% "circe-core" % "0.12.2" ::
        "io.circe" %%% "circe-generic" % "0.12.2" % "test" ::
        "org.scalatest" %%% "scalatest" % "3.0.8" % "test" ::
        Nil,
    name := "circe-validation"
  )

lazy val circeValidationJVM = circeValidation.jvm

lazy val circeValidationJS = circeValidation.js

// https://stackoverflow.com/questions/54392266/using-macro-paradise-and-cross-compiling-with-2-12-2-13
def compileWithMacroParadise: Command =
  Command.command("compileWithMacroParadise") { state =>
    import Project._
    val extractedState = extract(state)
    val stateWithMacroParadise =
      CrossVersion.partialVersion(extractedState.get(scalaVersion)) match {
        case Some((2, n)) if n >= 13 =>
          extractedState.appendWithSession(
            Seq(Compile / scalacOptions += "-Ymacro-annotations"),
            state
          )
        case _ =>
          extractedState
            .appendWithSession(
              addCompilerPlugin(
                "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
              ),
              state
            )
      }
    val (stateAfterCompileWithMacroParadise, _) =
      extract(stateWithMacroParadise)
        .runTask(Compile / compile, stateWithMacroParadise)
    stateAfterCompileWithMacroParadise
  }
