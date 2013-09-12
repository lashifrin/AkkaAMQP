package com.cisco.phalanx.amqp

import akka.actor._
import com.rabbitmq.client.Channel

object Sender extends App {
  val system = ActorSystem("sender")
  val sendingChannel = AMQPConnection.getConnection().createChannel()
  sendingChannel.queueDeclare(AMQPConnectionConfig.RABBITMQ_QUEUE, false, false, false, null)
  val sender = system.actorOf(Props(new AMQPPublisher(sendingChannel, AMQPConnectionConfig.RABBITMQ_QUEUE)))
  // (1 to 1) map { i => sender ! "Hello World" }
  sender ! "Hello World"

}

object StartListener extends App {
  val system = ActorSystem("listener")
  val listener = system.actorOf(Props(new AMQPListener(AMQPConnection.getConnection().createChannel(), AMQPConnectionConfig.RABBITMQ_QUEUE)))
}