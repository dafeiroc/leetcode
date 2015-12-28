import sbt._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys


object Formatting {
  lazy val formatSettings: Seq[Setting[_]] =
    SbtScalariform.scalariformSettings ++ Seq(
      ScalariformKeys.preferences in Compile  := formattingPreferences,
      ScalariformKeys.preferences in Test     := formattingPreferences
    )

  lazy val docFormatSettings: Seq[Setting[_]] =
    SbtScalariform.scalariformSettings ++ Seq(
      ScalariformKeys.preferences in Compile  := docFormattingPreferences,
      ScalariformKeys.preferences in Test     := docFormattingPreferences
    )

  def formattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(RewriteArrowSymbols, true)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
  }

  def docFormattingPreferences = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(RewriteArrowSymbols, false)
      .setPreference(AlignParameters, true)
      .setPreference(AlignSingleLineCaseStatements, true)
  }
}