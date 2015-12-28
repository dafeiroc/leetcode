import sbt._
import sbt.Keys._

object Publish {

  lazy val settings: Seq[Setting[_]] =
    publishConfigurationSettings

  lazy val publishConfigurationSettings: Seq[Setting[_]] =
    Seq(
      crossPaths              := true,
      credentials            ++= Configuration.credentials,
      organizationName        := Configuration.organizationName,
      organizationHomepage    := Configuration.organizationHomepage,
      licenses                := Configuration.licenses,
      publishMavenStyle       := true,
      pomIncludeRepository    := { _ => false },
      publishArtifact in Test := false
    )

}
