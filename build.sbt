import scala.collection.Seq

name := "UserService"

version := "1.0-SNAPSHOT"


lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.14"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
libraryDependencies ++= Seq(
  "org.playframework" %% "play-slick"            % "6.1.0",
  "org.playframework" %% "play-slick-evolutions" % "6.1.0",
  "org.apache.kafka" % "kafka-clients" % "2.8.0",
  "mysql" % "mysql-connector-java" % "8.0.26",
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.3.4",
//  "com.typesafe.play" %% "play" % "2.8.15",
  "com.typesafe.play" %% "play-json" % "2.9.2"
)

