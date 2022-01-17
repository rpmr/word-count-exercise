import Deps._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name               := "coralogix",
    fork               := true,
    run / connectInput := true,
    libraryDependencies ++= Cats.all ++ CatsEffect.all ++ Circe.all ++ Fs2.all ++ Http4s.all ++
      Log4cats.all ++ Other.all ++ Pureconfig.all ++ Tapir.all ++ Testing.all
  )

inThisBuild(
  List(
    scalaVersion                                   := "2.13.8",
    semanticdbEnabled                              := true,
    semanticdbVersion                              := scalafixSemanticdb.revision,
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0",
    scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)
  )
)
