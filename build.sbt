name := """play-scala-API"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
	guice,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.13.0-play26",
  "com.typesafe.play" %% "play-json-joda" % "2.6.8",
  jodaForms
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
