import sbt._

import Keys._

object Scalendar extends Build {

  val scalendarSettings = Defaults.defaultSettings ++ Seq (
    organization := "com.github.philcali",
    version := "0.0.4",
    scalaVersion := "2.9.0",
    crossScalaVersions := Seq ("2.9.0", "2.8.1", "2.8.0"),  
    publishTo := Some("Scala Tools Nexus" 
                   at "http://nexus.scala-tools.org/content/repositories/releases/"),
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
    publishMavenStyle := true
  )

  val tested = Seq (
    libraryDependencies <+= (scalaVersion) {
      case v if v contains "2.8" => 
        "org.scalatest" % "scalatest" % "1.3" % "test"
      case _ =>
        "org.scalatest" %% "scalatest" % "1.4.1" % "test"
    }
  ) 

  lazy val scalendar = Project (
    "scalendar",
    file("."),
    settings = scalendarSettings 
  ) aggregate (library, future)

  lazy val library = Project (
    "library",
    file("library"),
    settings = scalendarSettings ++ tested 
  )
 
  lazy val future = Project (
    "future",
    file("future"),
    settings = scalendarSettings
  ) dependsOn (library)
}
