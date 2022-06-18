val scala3Version = "3.1.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "git",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.12",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % "test"
  )
