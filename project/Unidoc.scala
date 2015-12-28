import sbt._
import sbt.Keys._
import sbtunidoc.Plugin.UnidocKeys._
import sbtunidoc.Plugin.{ ScalaUnidoc, JavaUnidoc, scalaJavaUnidocSettings, genjavadocSettings, scalaUnidocSettings }
import scala.annotation.tailrec


object Unidoc {
  def settings(ignoreAggregates: Seq[Project], ignoreProjects: Seq[Project]) = {
    val withoutAggregates = ignoreAggregates.foldLeft(inAnyProject) { _ -- inAggregates(_, transitive = true, includeRoot = true) }
    val docProjectFilter = ignoreProjects.foldLeft(withoutAggregates) { _ -- inProjects(_) }

    inTask(unidoc)(Seq(
      unidocProjectFilter in ScalaUnidoc := docProjectFilter,
      unidocProjectFilter in JavaUnidoc := docProjectFilter,
      apiMappings in ScalaUnidoc := (apiMappings in (Compile, doc)).value
    ))
  }

  val (unidocSettings, javadocSettings) =
    if (Configuration.genjavadocEnabled) (scalaJavaUnidocSettings, genjavadocSettings)
    else (scalaUnidocSettings, Nil)

  def scaladocSettings: Seq[Setting[_]] = {
    scaladocSettingsNoVerificationOfDiagrams ++
      (if (Configuration.scaladocDiagramsEnabled) Seq(doc in Compile ~= scaladocVerifier) else Seq.empty)
  }

  // for projects with few (one) classes there might not be any diagrams
  def scaladocSettingsNoVerificationOfDiagrams: Seq[Setting[_]] = {
    inTask(doc)(Seq(
      scalacOptions in Compile <++= (version, baseDirectory in ThisBuild) map scaladocOptions,
      autoAPIMappings := Configuration.scaladocAutoAPI
    ))
  }

  def scaladocOptions(ver: String, base: File): List[String] = {
    val urlString = Configuration.scmInfo.map(_.browseUrl + "/tree/master/€{FILE_PATH}.scala").getOrElse("")
    val opts = List("-implicits", "-doc-source-url", urlString, "-sourcepath", base.getAbsolutePath)
    if (Configuration.scaladocDiagramsEnabled) "-diagrams"::opts else opts
  }

  def scaladocVerifier(file: File): File= {
    @tailrec
    def findHTMLFileWithDiagram(dirs: Seq[File], diagramFound: Boolean): Boolean = (dirs, diagramFound) match {
      case (_, true) => true
      case (d, _) if d.isEmpty => false
      case (_, false) =>
        val curr = dirs.head
        val (newDirs, files) = curr.listFiles.partition(_.isDirectory)
        val rest = dirs.tail ++ newDirs
        val hasDiagram = files exists { f =>
          val name = f.getName
          if (name.endsWith(".html") && !name.startsWith("index-") &&
            !name.equals("index.html") && !name.equals("package.html")) {
            val source = scala.io.Source.fromFile(f)(scala.io.Codec.UTF8)
            val hd = try source.getLines().exists(_.contains("<div class=\"toggleContainer block diagram-container\" id=\"inheritance-diagram-container\">"))
            catch {
              case e: Exception => throw new IllegalStateException("Scaladoc verification failed for file '"+f+"'", e)
            } finally source.close()
            hd
          }
          else false
        }
        findHTMLFileWithDiagram(rest, hasDiagram)
    }

    // if we have generated scaladoc and none of the files have a diagram then fail
    if (file.exists() && !findHTMLFileWithDiagram(List(file), diagramFound = false))
      sys.error("ScalaDoc diagrams not generated!")
    else
      file
  }
}
