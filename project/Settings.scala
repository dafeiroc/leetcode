import sbt._
import sbt.Keys._


object Settings {

  lazy val baseSettings =
    projectSettings

  lazy val buildSettings = Seq(
    organization        := Configuration.organization,
    scalaVersion        := Configuration.scalaVersion,
    crossScalaVersions  := Configuration.crossScalaVersions
  )

  lazy val projectSettings = Seq(
    name        := Configuration.name,
    description := Configuration.description,
    homepage    := Configuration.homepage,
    startYear   := Configuration.startYear
  )

  lazy val noPublishSettings = Seq(
    publish         := { },
    publishLocal    := { },
    publishArtifact := false
  )

  lazy val compilationSettings = Seq(
    javacOptions in Compile ++= Seq(
      "-encoding", "UTF-8",
      "-source", Configuration.jvmVersion,
      "-target", Configuration.jvmVersion,
      "-Xlint:unchecked",
      "-XDignore.symbol.file"
    ),
    javacOptions in Compile ++= (if (Configuration.allWarnings) Seq("-Xlint:deprecation") else Nil),
    scalacOptions in Compile ++= Seq(
      "-encoding", "utf8",
      "-g:vars",
      "-feature",
      "-unchecked",
      "-Xfatal-warnings",
      "-Xlog-reflective-calls",
      "-Xfuture",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-inaccessible"
    ),
    scalacOptions in Compile ++= (if (Configuration.allWarnings) Seq("-deprecation") else Nil),
    incOptions := incOptions.value.withNameHashing(true)
  )

  val resolverSettings = Seq(
    resolvers ++= Resolvers.common
  )

  lazy val parentSettings =
    baseSettings ++ noPublishSettings

  lazy val formatSettings =
    Formatting.formatSettings

  lazy val unidocSettings =
    Unidoc.unidocSettings

  lazy val scaladocSettings =
    Unidoc.scaladocSettings

  lazy val publishSettings =
    Publish.settings

  lazy val releaseSettings =
    Release.settings

  lazy val aspectjSettings =
    AspectJ.settings

  lazy val revolverSettings =
    spray.revolver.RevolverPlugin.Revolver.settings

  lazy val defaultSettings =
    baseSettings ++
    resolverSettings ++
    compilationSettings ++
    publishSettings ++
    Seq(
      parallelExecution in Test := Configuration.parallelExecution,
      // full stack traces and case durations
      testOptions in Test += Tests.Argument("-oDF")
    )
}
