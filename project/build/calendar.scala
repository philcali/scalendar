import sbt._

class Project(info: ProjectInfo) extends ParentProject(info) {

  class CalendarProject(info: ProjectInfo) extends DefaultProject(info)
  
  trait Tested extends CalendarProject {
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

  lazy val library = project("library", "library", new CalendarProject(_) with Tested)
  lazy val future = project("future", "implicits", new CalendarProject(_) with Only28Up, library)
 
  override def dependencies = super.dependencies.filter {
    case _: Only28Up if buildScalaVersion startsWith "2.7" => false 
    case _ => true
  }
 
  override def managedStyle = ManagedStyle.Maven
  val publishTo = "Scala Tools Nexus" at 
                  "http://nexus.scala-tools.org/content/repositories/releases/"
 
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)   
}
