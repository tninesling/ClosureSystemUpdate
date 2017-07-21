name := """CanonicalDirectBasis"""

version := "0.0.1-SNAPSHOT"

lazy val root = project in file(".")

scalaVersion := "2.11.7"

mainClass in (Compile, run) := Some("shep.basis.StatisticsGeneratingExample")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "org.slf4j" % "slf4j-jdk14" % "1.8.0-alpha2"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
