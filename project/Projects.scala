import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

import sbt._
import sbt.Keys._
import com.typesafe.sbt.packager.archetypes.JavaServerAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin


object Projects extends Build {
  import Dependencies._
  import Settings._

  loadSystemProperties("project/build.properties")

  lazy val root = Project(
      id = Configuration.name,
      base = file(".")
    )
    .settings(
      libraryDependencies ++=
        compile() ++
        test()
    )
    .settings(defaultSettings)
    .settings(formatSettings)
    .settings(noPublishSettings)
    .settings(revolverSettings)
    .enablePlugins(DockerPlugin)
    .enablePlugins(JavaServerAppPackaging)

  override lazy val settings =
    super.settings ++
    buildSettings ++
    resolverSettings

  private def module(id: String, dependencies: Seq[ModuleID] = Seq.empty) = {
    val name = Configuration.name + "-" + id
    Project(id = id, base = file(name))
      .settings(moduleName := name)
      .settings(libraryDependencies ++= dependencies)
  }

  def loadSystemProperties(fileName: String): Unit = {
    import scala.collection.JavaConverters._
    val file = new File(fileName)
    if (file.exists()) {
      println("Loading system properties from file `" + fileName + "`")
      val in = new InputStreamReader(new FileInputStream(file), "UTF-8")
      val props = new Properties
      props.load(in)
      in.close()
      sys.props ++ props.asScala
    }
  }
}
