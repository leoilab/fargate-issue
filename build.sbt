val Http4sVersion = "0.21.5"
val CirceVersion = "0.13.0"
val Specs2Version = "4.10.0"
val LogbackVersion = "1.2.3"
val CDKVersion = "1.53.0"

lazy val root = (project in file("."))
  .enablePlugins(DockerPlugin, PackPlugin)
  .settings(
    organization := "com.leoilab",
    name := "fargate-issue",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.2",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    docker := docker.dependsOn(pack).value,
    dockerfile in docker := {
      val targetLibDir = s"/opt/${name.value}/lib"

      new Dockerfile {
        from("amazoncorretto:8u252")
        add(file(s"${packTargetDir.value}/${packDir.value}/lib"), targetLibDir)
        cmdRaw(s"""java -cp "$targetLibDir/*" -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0 com.leoilab.fargateissue.Main""")
      }
    }
  )

lazy val cdk = (project in file("cdk"))
    .settings(
      scalaVersion := "2.13.2",
      libraryDependencies ++= Seq(
        "software.amazon.awscdk" % "ec2"          % CDKVersion,
        "software.amazon.awscdk" % "ecs"          % CDKVersion,
        "software.amazon.awscdk" % "ecs-patterns" % CDKVersion
      ),
      Compile / run := (Compile / run).dependsOn(docker in root).evaluated,
    )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings",
)
