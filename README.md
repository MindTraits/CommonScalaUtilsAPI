Reactivemongo for Play 2.6 play-scala-API
=======================
This is a play-scala-API using reactiveMongo driver. It demontrates:
<ul>
    <li>MongoDb Connection using reactiveMongoDb in Play</li>
    <li>BSONReader and BSONWriter Implementation</li>
</ul>

play-scala-API use the following:
<ul>
<li>Play Framework 2.6</li>
<li>Reactive Scala Driver for MongoDB 0.13.0-play26</li>
<li>Scala 2.12.4</li>
<li>MongoDb</li>
</ul>

Setup Instruction
=======================
Modify build.sbt
<div class="highlight highlight-scala"><pre>
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
</pre></div>


Run Instruction
=======================
<div class="highlight highlight-scala"><pre>
  [localhost@localhost play-scala-API]$ ./activator
  [play-scala-API] $ run

  To check API :
  1. Method        : Post 
     Url           : localhost:9000/addCountry
     requestObject : {"name":"India",
                      "code":"IN", 
                      "currency":{"name":"Indian Rupee","code":"INR"}
                     }
                     
     responseObject: {
                          "result": "success",
                          "data": {
                              "name": "India",
                              "code": "IN",
                              "currency": {
                                  "name": "Indian Rupee",
                                  "code": "INR"
                              }
                          }
                      }
</pre></div>
