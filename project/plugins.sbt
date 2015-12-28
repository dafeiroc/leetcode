resolvers += "sonatype-releases"   at "http://oss.sonatype.org/content/repositories/releases"
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.eed3si9n"       % "sbt-unidoc"            % "0.3.2")
addSbtPlugin("com.eed3si9n"       % "sbt-assembly"          % "0.13.0")
addSbtPlugin("com.typesafe.sbt"   % "sbt-aspectj"           % "0.10.2")
addSbtPlugin("com.github.gseitz"  % "sbt-release"           % "1.0.0")
addSbtPlugin("com.typesafe.sbt"   % "sbt-native-packager"   % "1.0.1")
addSbtPlugin("com.jsuereth"       % "sbt-pgp"               % "1.0.0")
addSbtPlugin("com.typesafe.sbt"   % "sbt-aspectj"           % "0.10.2")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"         % "1.0.4")
addSbtPlugin("com.typesafe.sbt"   % "sbt-scalariform"       % "1.3.0")
addSbtPlugin("org.scalastyle"     % "scalastyle-sbt-plugin" % "0.7.0")
addSbtPlugin("com.timushev.sbt"   % "sbt-updates"           % "0.1.8")
addSbtPlugin("io.spray"           % "sbt-revolver"          % "0.7.2")
