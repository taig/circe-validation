addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

enablePlugins(TutPlugin)

crossScalaVersions ++=
  "2.11.11" ::
    scalaVersion.value ::
    Nil

libraryDependencies ++=
  "io.circe" %% "circe-core" % "0.8.0" ::
    "io.circe" %% "circe-generic" % "0.8.0" % "test" ::
    "org.scalatest" %% "scalatest" % "3.0.3" % "test" ::
    "io.circe" %% "circe-generic" % "0.8.0" % "tut" ::
    "io.circe" %% "circe-parser" % "0.8.0" % "tut" ::
    Nil

name := "circe-validation"

organization := "io.taig"

scalacOptions ++=
  "-feature" ::
    "-language:implicitConversions" ::
    Nil

scalaVersion := "2.12.3"

tutTargetDirectory := baseDirectory.value
