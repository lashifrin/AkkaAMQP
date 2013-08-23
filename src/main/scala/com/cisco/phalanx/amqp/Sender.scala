package com.cisco.phalanx.amqp

import akka.actor._
import com.rabbitmq.client.Channel

object Sender extends App {
  val system = ActorSystem("sender")
  val listener = system.actorOf(Props(new AMQPListener(AMQPConnection.getConnection().createChannel(), AMQPConnectionConfig.RABBITMQ_QUEUE)))

  startSending

  def startSending = {
    val connection = AMQPConnection.getConnection()
    val sendingChannel = connection.createChannel()
    sendingChannel.queueDeclare(AMQPConnectionConfig.RABBITMQ_QUEUE, false, false, false, null)
    val sender = system.actorOf(Props(new AMQPPublisher(channel = sendingChannel, queue = AMQPConnectionConfig.RABBITMQ_QUEUE)))
    sender ! "Hello World"
  }
}