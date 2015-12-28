import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtAspectj._
import com.typesafe.sbt.SbtAspectj.AspectjKeys._


object AspectJ {

  lazy val settings = aspectjSettings ++ Seq(
    aspectjVersion            := Dependencies.Versions.aspectj,
    compileOnly in Aspectj    := true,
    fork in Test              := true,
    javaOptions in Test     <++= weaverOptions in Aspectj,
    javaOptions in run      <++= weaverOptions in Aspectj,
    lintProperties in Aspectj += "invalidAbsoluteTypeName = ignore"
  )

}
