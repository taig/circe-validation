import sbt._
import sbt.Keys._

object Settings {
  val common: Seq[Def.Setting[_]] = Def.settings(
    crossScalaVersions ++=
      "2.12.10" ::
        scalaVersion.value ::
        Nil,
    organization := "io.taig",
    scalacOptions ++=
      "-feature" ::
        "-language:implicitConversions" ::
        Nil,
    scalaVersion := "2.13.1"
  )
}
