import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) {
  def scalatestVersion = buildScalaVersion match {
    case v if v contains "2.9" => 
      "org.scalatest" % "scalatest_%s".format(v) % "1.4.1"
    case v if v contains "2.8" => 
      "org.scalatest" % "scalatest" % "1.3"
    case _ => "org.scalatest" % "scalatest" % "1.1"
  }

  val scalatest = scalatestVersion % "test" 

  override def managedStyle = ManagedStyle.Maven
  val publishTo = "Scala Tools Nexus" at 
                  "http://nexus.scala-tools.org/content/repositories/releases/"
 
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)   
}
