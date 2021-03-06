name := "Sevstone Import Tool"

version := "1.0"

scalaVersion := "2.10.2"

scalacOptions += "-deprecation"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.13"

libraryDependencies += "com.typesafe.slick" %% "slick" % "1.0.1"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.21"

libraryDependencies += "postgresql" % "postgresql" % "9.1-901.jdbc4"
