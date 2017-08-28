import sbt._
import sbt.Keys._

object Settings {
  val common = Def.settings(
    addCompilerPlugin(
      "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
    crossScalaVersions ++=
      "2.11.11" ::
        scalaVersion.value ::
        Nil,
    organization := "io.taig",
    scalacOptions ++=
      "-feature" ::
        "-language:implicitConversions" ::
        Nil,
    scalaVersion := "2.12.3"
  )
}