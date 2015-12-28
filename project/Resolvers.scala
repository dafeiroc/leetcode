import sbt._


object Resolvers {
  val sunRepo              = "Sun Maven2 Repo"         at "http://download.java.net/maven/2"
  val oracleRepo           = "Oracle Maven2 Repo"      at "http://download.oracle.com/maven"
  val sonatypeGithubRepo   = "Sonatype Github Repo"    at "http://oss.sonatype.org/content/repositories/github-releases"
  val sonatypeReleaseRepo  = "Sonatype Release Repo"   at "http://oss.sonatype.org/content/repositories/releases"
  val sonatypeSnapshotRepo = "Sonatype Snapshot Repo"  at "http://oss.sonatype.org/content/repositories/snapshots"
  val typesafeReleaseRepo  = "Typesafe Release Repo"   at "http://repo.typesafe.com/typesafe/releases/"
  val bintraySbtPluginRepo = "Bintray sbt plugin Repo" at "https://dl.bintray.com/content/sbt/sbt-plugin-releases"
  val bintrayNonRepo       = "bintray/non"             at "http://dl.bintray.com/non/maven"
  val bintrayPjanRepo      = "pjan at bintray"         at "http://dl.bintray.com/pjan/maven"

  val common: Seq[MavenRepository] = Seq(
    sunRepo,
    oracleRepo,
    sonatypeReleaseRepo,
    sonatypeSnapshotRepo,
    typesafeReleaseRepo,
    bintraySbtPluginRepo,
    bintrayNonRepo,
    bintrayPjanRepo
  )
}
