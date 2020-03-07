resolvers += Resolver.bintrayRepo("colisweb", "sbt-plugins")

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.3")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.6.0-RC1")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.4")
// Java agent needed for ObjectSizer
addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.5")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.10")