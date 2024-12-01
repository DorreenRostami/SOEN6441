name := "assign2"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .enablePlugins(JacocoPlugin)

ThisBuild / javacOptions ++= Seq("--release", "11")
scalaVersion := "2.13.15"

libraryDependencies += guice

// Google API for YouTube
libraryDependencies += "com.google.apis" % "google-api-services-youtube" % "v3-rev222-1.25.0"

// JUnit and Mockito for testing
libraryDependencies ++= Seq(
  "org.junit.jupiter" % "junit-jupiter" % "5.10.3" % Test,
  "org.junit.vintage" % "junit-vintage-engine" % "5.10.3" % Test,
  "org.mockito" % "mockito-core" % "5.11.0" % Test,
  "org.mockito" %% "mockito-scala" % "1.17.37" % Test,
  "org.mockito" %% "mockito-scala-scalatest" % "1.17.37" % Test,
  "org.mockito" % "mockito-inline" % "5.2.0" % Test
)

// Unified Akka dependencies
lazy val akkaVersion = "2.8.6"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.5"

// Test and documentation settings
Test / fork := false
Compile / doc / scalacOptions ++= Seq("-private")
