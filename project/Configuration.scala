import sbt._


object Configuration {

  // project settings
  val name                  = "leetcode"
  val description           = ""
  val homepage              = Some(url("http://pengfeiz.github.io"))
  val startYear             = Some(2015)

  // compilation settings
  val jvmVersion            = "1.8"

  // build settings
  val organization          = ""
  val scalaVersion          = "2.11.7"
  val crossScalaVersions    = Seq("2.11.7")

  // publish settings
  val organizationName      = "pengfeiz"
  val organizationHomepage  = Some(url("http://pengfeiz.github.io"))
  val licenses              = Seq(("Proprietary", url("http://pengfeiz.github.io")))
  val developers            = List()
  val scmInfo               = Some(ScmInfo(browseUrl = url("http://github.com/pengfeiz/leetcode"), connection = "scm:git:git@github.com/pengfeiz/leetcode.git"))
  val publishCredentials    = None // Some(Seq(Credentials(Path.userHome / ".ivy2" / ".credentials")))

  // Loading the dynamic settings
  final def parallelExecution: Boolean       = sys.props.getOrElse("parallelExecution",        "true").toBoolean
  final def allWarnings: Boolean             = sys.props.getOrElse("allwarnings",              "false").toBoolean
  final def scaladocDiagramsEnabled: Boolean = sys.props.getOrElse("scaladoc.diagrams",        "true").toBoolean
  final def scaladocAutoAPI: Boolean         = sys.props.getOrElse("scaladoc.autoapi",         "true").toBoolean
  final def genjavadocEnabled: Boolean       = sys.props.getOrElse("genjavadoc.enabled",       "false").toBoolean
  final def bintrayReleaseOnPublish: Boolean = sys.props.getOrElse("bintray.releaseonpublish", "false").toBoolean

  final def credentials: Seq[Credentials] =
    sys.props.get("publish.credentials").map(f => Seq(Credentials(new File(f))))
        .orElse(publishCredentials)
        .getOrElse(Seq.empty[Credentials])

}
