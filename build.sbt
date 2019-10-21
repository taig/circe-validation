import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

val circeVersion = "0.12.2"
val scalatestVersion = "3.0.8"

lazy val root = project
  .in(file("."))
  .settings(noPublishSettings)
  .settings(
    description := "Use cats Validated to create (Accumulating) circe Decoders"
  )
  .aggregate(core.jvm, core.js)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(sonatypePublishSettings)
  .settings(
    libraryDependencies ++=
      "io.circe" %%% "circe-core" % circeVersion ::
        "io.circe" %%% "circe-generic" % circeVersion % "test" ::
        "org.scalatest" %%% "scalatest" % scalatestVersion % "test" ::
        Nil,
    name := "circe-validation",
    normalizedName := name.value
  )

lazy val website = project
  .enablePlugins(MicrositesPlugin)
  .settings(micrositeSettings)
  .settings(
    libraryDependencies ++=
      "io.circe" %% "circe-generic" % circeVersion % Compile ::
        "io.circe" %% "circe-parser" % circeVersion % Compile ::
        Nil,
    mdocVariables ++= {
      val format: String => String =
        version => s"`${version.replaceAll("\\.\\d+$", "")}`"

      Map(
        "NAME" -> micrositeName.value,
        "MODULE" -> (core.jvm / normalizedName).value,
        "SCALA_VERSIONS" -> crossScalaVersions.value.map(format).mkString(", "),
        "SCALAJS_VERSION" -> format(scalaJSVersion)
      )
    },
    micrositeDescription := (root / description).value,
    micrositeName := "circe Validation"
  )
  .dependsOn(core.jvm)
