lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """corona-infection-data""",
    organization := "com.example",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.10",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
    ),
    scalacOptions ++= Seq( 
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )