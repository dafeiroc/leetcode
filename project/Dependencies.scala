import sbt._


object Dependencies {

  object Versions {
    val akka                    = "2.3.11"
    val akkaHttp                = "1.0-RC4"
    val akkaStream              = "1.0-RC4"
    val aspectj                 = "1.8.6"
    val logback                 = "1.1.3"
    val play                    = "2.4.1"
    val scalactic               = "2.2.5"
    val typesafeConfig          = "1.2.1"
    val playJsonScalactic       = "0.1.0"
    val akkaHttpPlayMarshalling = "0.2.0"

    val paidyCommon             = "1.3.2-SNAPSHOT"

    val scalaTest               = "2.2.5"
    val scalaMock               = "3.2.2"
    val scalaCheck              = "1.12.2"
  }

  val akkaActor               = "com.typesafe.akka"              %%  "akka-actor"                           % Versions.akka
  val akkaHttp                = "com.typesafe.akka"              %%  "akka-http-experimental"               % Versions.akkaHttp
  val akkaHttpCore            = "com.typesafe.akka"              %%  "akka-http-core-experimental"          % Versions.akkaHttp
  val akkaHttpXml             = "com.typesafe.akka"              %%  "akka-http-xml-experimental"           % Versions.akkaHttp
  val akkaSlf4j               = "com.typesafe.akka"              %%  "akka-slf4j"                           % Versions.akka
  val akkaStream              = "com.typesafe.akka"              %%  "akka-stream-experimental"             % Versions.akkaStream
  val logback                 = "ch.qos.logback"                 %   "logback-classic"                      % Versions.logback
  val playJson                = "com.typesafe.play"              %%  "play-json"                            % Versions.play
  val scalactic               = "org.scalactic"                  %%  "scalactic"                            % Versions.scalactic
  val typesafeConfig          = "com.typesafe"                   %   "config"                               % Versions.typesafeConfig
  val playJsonScalactic       = "io.pjan"                        %%  "play-json-scalactic"                  % Versions.playJsonScalactic
  val akkaHttpPlayMarshalling = "io.pjan"                        %%  "akka-http-marshalling-play-json"      % Versions.akkaHttpPlayMarshalling
  val paidyCommonUtils        = "com.paidy"                      %%  "paidy-common-utils"                   % Versions.paidyCommon
  val paidyCommonDomain       = "com.paidy"                      %%  "paidy-common-domain"                  % Versions.paidyCommon
  val paidyCommonDao          = "com.paidy"                      %%  "paidy-common-dao"                     % Versions.paidyCommon


  val akkaHttpTest       = "com.typesafe.akka"              %%  "akka-http-testkit-experimental"       % Versions.akkaHttp
  val akkaTest           = "com.typesafe.akka"              %%  "akka-testkit"                         % Versions.akka
  val scalaCheck         = "org.scalacheck"                 %%  "scalacheck"                           % Versions.scalaCheck
  val scalaMock          = "org.scalamock"                  %%  "scalamock-scalatest-support"          % Versions.scalaMock
  val scalaTest          = "org.scalatest"                  %%  "scalatest"                            % Versions.scalaTest
  val paidyCommonTestkit = "com.paidy"                      %%  "paidy-common-testkit"                 % Versions.paidyCommon

  def compile    (modules: ModuleID*): Seq[ModuleID] = modules map (_ % "compile")
  def provided   (modules: ModuleID*): Seq[ModuleID] = modules map (_ % "provided")
  def test       (modules: ModuleID*): Seq[ModuleID] = modules map (_ % "test")
  def runtime    (modules: ModuleID*): Seq[ModuleID] = modules map (_ % "runtime")
  def container  (modules: ModuleID*): Seq[ModuleID] = modules map (_ % "container")
  def compiler   (modules: ModuleID*): Seq[ModuleID] = modules map (compilerPlugin(_))

}
