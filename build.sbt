
lazy val aggregatedProjects: Seq[ProjectReference] = Seq(
  `csw-yashoassembly`,
  `csw-yashohcd`,
  `csw-yashodeploy`
)

lazy val `yasho-root` = project
  .in(file("."))
  .aggregate(aggregatedProjects: _*)

// assembly module
lazy val `csw-yashoassembly` = project
  .settings(
    libraryDependencies ++= Dependencies.Yashoassembly
  )

// hcd module
lazy val `csw-yashohcd` = project
  .settings(
    libraryDependencies ++= Dependencies.Yashohcd
  )

// deploy module
lazy val `csw-yashodeploy` = project
  .dependsOn(
    `csw-yashoassembly`,
    `csw-yashohcd`
  )
  .enablePlugins(CswBuildInfo)
  .settings(
    libraryDependencies ++= Dependencies.YashoDeploy
  )
