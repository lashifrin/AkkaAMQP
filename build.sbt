name := "AkkaAMQP"

version := "1.0"

organization  := "cisco"

scalaVersion :=  "2.10.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.rabbitmq" % "amqp-client" % "3.1.4"

libraryDependencies +="com.typesafe.akka" %% "akka-actor" % "2.2.0"