name := "Sevstone Import Tool"

version := "1.0"

scalaVersion := "2.10.1"

scalacOptions += "-deprecation"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.13"

libraryDependencies += "com.typesafe.slick" % "slick_2.10" % "1.0.1-RC1"
