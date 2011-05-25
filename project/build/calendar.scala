import sbt._

class Project(info: ProjectInfo) extends ParentProject(info) {

  class Tested(info: ProjectInfo) extends DefaultProject(info) {
    def scalatestVersion = buildScalaVersion match {
      case v if v contains "2.9" => 
        "org.scalatest" % "scalatest_%s".format(v) % "1.4.1"
      case v if v contains "2.8" => 
        "org.scalatest" % "scalatest" % "1.3"
      case _ => "org.scalatest" % "scalatest" % "1.1"
    }

    lazy val scalatest = scalatestVersion % "test" 
  }

  trait Only28Up
  trait Only27

  lazy val library = project("library", "library", new Tested(_))
  lazy val legacy = project("legacy", "legacy implicits", new Tested(_) with Only27, library)
  lazy val future = project("future", "implicits", new Tested(_) with Only28Up, library)
 
  override def dependencies = super.dependencies.filter {
    case _: Only28Up if buildScalaVersion startsWith "2.7" => false 
    case _: Only27 => if (buildScalaVersion startsWith "2.7") true else false
    case _ => true
  }
 
  override def managedStyle = ManagedStyle.Maven
  val publishTo = "Scala Tools Nexus" at 
                  "http://nexus.scala-tools.org/content/repositories/releases/"
 
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)   
}
