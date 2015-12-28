import com.typesafe.sbt.pgp.PgpKeys
import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin.autoImport._


object Release {
  lazy val settings: Seq[Setting[_]] = Seq(
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    releaseCrossBuild := true
  )
}
