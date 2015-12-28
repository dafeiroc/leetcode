import sbt._
import sbt.Keys._


object Package {

  lazy val settings: Seq[Setting[_]] = Seq.empty

  lazy val resourceSettings: Seq[Setting[_]] = {
    val excludedResources = Seq("application.conf", "logback.xml")
    Seq(
      mappings in (Compile, packageBin) ~= { _.filterNot { case (_, fileName) =>
        excludedResources.contains(fileName)
      }}
    )
  }
}
