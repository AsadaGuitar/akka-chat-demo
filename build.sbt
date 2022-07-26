ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val akkaVersion       = "2.6.19"
val akkaHttpVersion   = "10.2.9"
val logbackVersion    = "1.2.9"


lazy val client = (project in file("client"))
  .settings(
    name := "akka_websocket_demo_client"
  )

lazy val server = (project in file("server"))
  .settings(
    name := "akka_websocket_demo_server",
    libraryDependencies ++= Seq(
      // actor
      "com.typesafe.akka"         %% "akka-actor"                          % akkaVersion,
      // http
      "com.typesafe.akka"         %% "akka-http"                           % akkaHttpVersion,
      // streams
      "com.typesafe.akka"         %% "akka-stream"                         % akkaVersion,
      // logging
      "com.typesafe.akka"         %% "akka-slf4j"                          % akkaVersion,
      "ch.qos.logback"            %  "logback-classic"                     % logbackVersion
    )
  )