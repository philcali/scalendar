import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  val scalatest = "org.scalatest" % "scalatest" % "1.2"

  override def managedStyle = ManagedStyle.Maven
  val publishTo = "Scala Tools Nexus" at 
                  "http://nexus.scala-tools.org/content/repositories/releases/"
 
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)   
}
