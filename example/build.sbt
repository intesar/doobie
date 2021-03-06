
libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql"          % "9.3-1102-jdbc41",
  "com.h2database" %  "h2"                 % "1.3.170",
  "io.argonaut"    %% "argonaut"           % "6.1-M4",
  "org.http4s"     %% "http4s-core"        % "0.3.0",
  "org.http4s"     %% "http4s-dsl"         % "0.3.0",
  "org.http4s"     %% "http4s-blazeserver" % "0.3.0",
  "org.http4s"     %% "http4s-argonaut"    % "0.3.0"
)

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.11.5" % "test",
  "org.specs2"     %% "specs2"     % "2.4"    % "test"
)

scalacOptions ++= Seq(
  "-deprecation",
  "-Xfatal-warnings"
)

