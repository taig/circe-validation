addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

lazy val root = project
  .in(file("."))
  .enablePlugins(TutPlugin)
  .settings(
    publish := {},
    publishLocal := {},
    libraryDependencies ++=
      "io.circe" %% "circe-generic" % "0.8.0" % "tut" ::
        "io.circe" %% "circe-parser" % "0.8.0" % "tut" ::
        Nil,
    tutSourceDirectory := baseDirectory.value / "tut",
    tutTargetDirectory := baseDirectory.value
  )
  .dependsOn(circeValidationJVM)
  .aggregate(circeValidationJVM, circeValidationJS)

lazy val circeValidation = crossProject
  .in(file("."))
  .settings(
    crossScalaVersions ++=
      "2.11.11" ::
        scalaVersion.value ::
        Nil,
    libraryDependencies ++=
      "io.circe" %%% "circe-core" % "0.8.0" ::
        "io.circe" %%% "circe-generic" % "0.8.0" % "test" ::
        "org.scalatest" %%% "scalatest" % "3.0.3" % "test" ::
        Nil,
    name := "circe-validation",
    organization := "io.taig",
    scalacOptions ++=
      "-feature" ::
        "-language:implicitConversions" ::
        Nil,
    scalaVersion := "2.12.3"
  )

lazy val circeValidationJVM = circeValidation.jvm

lazy val circeValidationJS = circeValidation.js
