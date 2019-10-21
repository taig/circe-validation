lazy val root = project
  .in(file("."))
  .settings(noPublishSettings)
  .settings(
    description := "Use cats Validated to create (Accumulating) circe Decoders"
  )
  .aggregate(coreJVM, coreJS)

lazy val core = crossProject
  .in(file("."))
  .settings(sonatypePublishSettings)
  .settings(
    libraryDependencies ++=
      "io.circe" %%% "circe-core" % "0.12.2" ::
        "io.circe" %%% "circe-generic" % "0.12.2" % "test" ::
        "org.scalatest" %%% "scalatest" % "3.0.8" % "test" ::
        Nil,
    name := "circe-validation"
  )

lazy val coreJVM = core.jvm

lazy val coreJS = core.js

lazy val website = project
  .enablePlugins(MicrositesPlugin)
  .settings(micrositeSettings)
  .settings(
    mdocVariables ++= {
      val format: String => String =
        version => s"`${version.replaceAll("\\.\\d+$", "")}`"

      Map(
        "MODULE" -> (core.jvm / normalizedName).value,
        "SCALA_VERSIONS" -> crossScalaVersions.value.map(format).mkString(", "),
        "SCALAJS_VERSION" -> format(scalaJSVersion)
      )
    },
    micrositeDescription := (root / description).value
  )
  .dependsOn(coreJVM)