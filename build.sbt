val scala3Version = "3.1.2"

val zioVersion = "2.0.0"
val scalaTestVersion = "3.2.12"
val scodecVersion = "2.2.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "git",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "dev.zio" %% "zio" % zioVersion,
    libraryDependencies += "dev.zio" %% "zio-test" % zioVersion,
    libraryDependencies += "dev.zio" %% "zio-streams" % zioVersion,
    libraryDependencies += "org.scalactic" %% "scalactic" % scalaTestVersion,
    libraryDependencies += "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    libraryDependencies += "org.scodec" %% "scodec-core" % scodecVersion,
    libraryDependencies += "org.scodec" %% "scodec-bits" % "1.1.34",


      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
