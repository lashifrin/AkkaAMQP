package com.cisco.phalanx.amqp

import akka.actor._
import com.rabbitmq.client.{ QueueingConsumer, Channel }

class AMQPListener(channel: Channel, queue: String) extends Actor {

  override def preStart = self ! "init"

  def receive = {
    case _ => startReceving
  }

  def startReceving = {
    val consumer = new QueueingConsumer(channel)
    channel.basicConsume(queue, true, consumer)
    while (true) {
      val delivery = consumer.nextDelivery()
      val msg = new String(delivery.getBody())
      println(msg)
    }
  }
}

class AMQPPublisher(channel: Channel, queue: String) extends Actor {
  def receive = {
    case msg: String => channel.basicPublish("", queue, null, msg.getBytes())
  }
}