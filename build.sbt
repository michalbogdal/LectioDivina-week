name := """play-getting-started"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= {

  val akkaV = "2.3.9"
  val sprayV = "1.3.3"

  Seq(
    jdbc,
    cache,
    "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
    ws,
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-json"    % sprayV,
    "io.spray"            %%  "spray-client"    % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "joda-time" % "joda-time"    % "2.3"
    , "org.joda"  % "joda-convert" % "1.6"
  )
}

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _ )
