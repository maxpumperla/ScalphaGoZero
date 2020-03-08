organization := "org.deeplearning4j"
name := "ScalphaGoZero"
version := "1.0.1"
description := "An independent implementation of DeepMind's AlphaGoZero in Scala, using Deeplearning4J (DL4J)"
licenses += "Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")

scalafmtOnCompile := true
connectInput in run := true

scalaVersion := "2.12.6"

// Jules note:
// -----------
// If warnings become a problem, this can be uncommented.
//scalacOptions := scalac= projectOptions.value.filter(_ != "-Xfatal-warnings")

// Use this if no GPU
lazy val dl4j = ((version: String) => Seq(
  "org.nd4j" % "nd4j-native-platform" % version,
  "org.deeplearning4j" % "deeplearning4j-core" % version
))("1.0.0-beta3")  // or beta6?

// use this if you have GPU with CUDA support.
// Check your CUDA version with nvcc --version. Supported versions are 9.0, 9.2, 10.0, 10.1, 10.2
//lazy val dl4j =  ((version: String) => Seq(
//  "org.nd4j" % "nd4j-cuda-10.2-platform" % version,
//  "org.deeplearning4j" % "deeplearning4j-core" % version,
//  "org.bytedeco" % "cuda-platform-redist" % "10.2-7.6-1.5.2"
//))("1.0.0-beta6")

// This is so we can compute the size of objects at runtime
enablePlugins(JavaAgent)
javaAgents += "org.spire-math" % "clouseau_2.12" % "0.2.2" % "compile;runtime"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
  "org.spire-math" % "clouseau_2.12" % "0.2.2", // for ObjectSizer
  "org.scalatest" %% "scalatest" % "3.1.0" % Test
) ++ dl4j

fork in run := true
javaOptions in run ++= Seq(
  "-Xms256M",
  "-Xmx1G",
  "-Dorg.bytedeco.javacpp.maxbytes=1G",
  "-Dorg.bytedeco.javacpp.maxphysicalbytes=3G", // Xmx + maxbytes + eps
  "-XshowSettings:vm",
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
