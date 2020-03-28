lazy val root =
  project
    .in(file("."))
    .settings(
      libraryDependencies ++= Seq(
        "com.lightbend.akka" %% "akka-stream-alpakka-s3" % "2.0.0-RC1",
        "com.typesafe.akka" %% "akka-stream" % "2.5.30",
        "com.typesafe.akka" %% "akka-http" % "10.1.11",
        "com.typesafe.akka" %% "akka-http-xml" % "10.1.11",
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.36.1" % "test",
        "org.scalatest" %% "scalatest" % "3.1.1" % "test"
      )
    )
