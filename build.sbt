name := "JobcoinMixer"

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  guice,
  ws
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)