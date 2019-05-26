organization := "org.deeplearning4j"
name := "ScalphaGoZero"
version := "1.0.0"
description := "An independent implementation of DeepMind's AlphaGoZero in Scala, using Deeplearning4J (DL4J)"
licenses += "Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")

scalafmtOnCompile := true
connectInput in run := true

scalaVersion := "2.12.6"

// Jules note:
// -----------
// I remove the `-Xfatal-warnings` from the scalac options set by the `sbt-tpolecat` plugin because it can be frustating.
// However, If you feel confident enough, it's better to activate it.
//
scalacOptions := scalacOptions.value.filter(_ != "-Xfatal-warnings")

lazy val dl4j = ((version: String) => Seq(
  "org.nd4j" % "nd4j-native-platform" % version,
  "org.deeplearning4j" % "deeplearning4j-core" % version
))("1.0.0-beta3")

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scala-lang.modules" %% "scala-xml" % "1.1.0",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
) ++ dl4j

fork in run := true
javaOptions in run ++= Seq(
  "-Xms4G",
  "-Xmx16G",
  "-XshowSettings:vm"
)

pomExtra := (
  <url>https://github.com/maxpumperla/ScalphaGoZero</url>
    <scm>
      <url>git@github.com:maxpumperla/ScalphaGoZero.git</url>
      <connection>scm:git:git@github.com:maxpumperla/ScalphaGoZero.git</connection>
    </scm>
    <developers>
      <developer>
        <id>maxpumperla</id>
        <name>Max Pumperla</name>
      </developer>
    </developers>
)
