organization := "com.github.philcali"

name := "scalendar"

version := "0.0.5"

publishTo := Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishMavenStyle := true

scalaVersion := "2.9.0"

crossScalaVersions := Seq ("2.9.0", "2.8.1", "2.8.0") 

publishArtifact in (Compile, packageDoc) := false

libraryDependencies <+= (scalaVersion) {
  case v if v contains "2.8" => 
    "org.scalatest" % "scalatest" % "1.3" % "test"
  case _ =>
    "org.scalatest" %% "scalatest" % "1.4.1" % "test"
}
