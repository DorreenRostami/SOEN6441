name := """assign1p2"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava).enablePlugins(JacocoPlugin)
ThisBuild / javacOptions ++= Seq("--release", "11")
scalaVersion := "2.13.15"

libraryDependencies += guice
libraryDependencies += "com.google.apis" % "google-api-services-youtube" % "v3-rev222-1.25.0"

libraryDependencies += "org.junit.jupiter" % "junit-jupiter" % "5.10.3" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "5.11.0" % Test
libraryDependencies += "org.mockito" %% "mockito-scala" % "1.17.37" % Test
libraryDependencies += "org.mockito" %% "mockito-scala-scalatest" % "1.17.37" % Test
libraryDependencies += "org.mockito" % "mockito-inline" % "5.2.0" % Test
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.8.5",       // Akka Actor
  "com.typesafe.akka" %% "akka-stream" % "2.8.6",      // Akka Streams
  "com.typesafe.akka" %% "akka-testkit" % "2.8.6" % Test // TestKit for Akka
)

fork in Test := false
scalacOptions in (Compile, doc) ++= Seq("-private")