name := """assign1p2"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
ThisBuild / javacOptions ++= Seq("--release", "11")
scalaVersion := "2.13.15"

libraryDependencies += guice
libraryDependencies += "com.google.apis" % "google-api-services-youtube" % "v3-rev222-1.25.0"

libraryDependencies += "org.junit.jupiter" % "junit-jupiter" % "5.10.3" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "5.11.0" % Test