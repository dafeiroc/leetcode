package io.pjan.sbt
package projectprompt

import sbt._
import sbt.Keys._


object ProjectpromptPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger = AllRequirements

  object autoImport extends ProjectpromptKeys

  import autoImport._

  override val globalSettings: Seq[Setting[_]] = Seq(
    projectpromptEnabled := true
  )

  override val projectSettings: Seq[Setting[_]] = Seq(
    shellPrompt <<= (projectpromptEnabled, name) { (displayProjectId, name) => (s: State) =>
      lazy val projectId = Project.extract(s).currentProject.id
      val prompt = if (displayProjectId) name + "::" + projectId else Nil
      prompt + "> "
    }
  )

}

trait ProjectpromptKeys {
  lazy val projectpromptEnabled = SettingKey[Boolean]("enables sbt prompt formatting to display the current project id")
}

object ProjectpromptKeys extends ProjectpromptKeys
