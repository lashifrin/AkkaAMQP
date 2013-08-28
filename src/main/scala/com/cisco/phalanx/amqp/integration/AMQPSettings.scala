package com.cisco.phalanx.amqp.integration

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.collection.JavaConverters._
import com.typesafe.config._
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Connection
import com.cisco.phalanx.amqp.AMQPConnectionConfig
import com.rabbitmq.client.QueueingConsumer

object Producer extends App {
  val config = ConfigFactory.load()
  val settings = new AMQPSettings(config.getConfig("akka.amqp.default"))
  val address = settings.addresses.map(com.rabbitmq.client.Address.parseAddress(_))
  val host = address.iterator.next.getHost()
  val port = address.iterator.next.getPort()

  val qConfig = config.getConfig("akka.amqp.splunk")

  val exName = qConfig.getString("exName")
  val passive = qConfig.getBoolean("passive")
  val exType = qConfig.getString("exType")

  val qName = qConfig.getString("qName")
  val durable = qConfig.getBoolean("durable")
  val exclusive = qConfig.getBoolean("exclusive")
  val autoDelete = qConfig.getBoolean("autoDelete")

  val connection = RabbitMQConnection.getConnection(host, port, settings)
  val sendingChannel = connection.createChannel()
  sendingChannel.exchangeDeclare(exName, exType, passive)
  sendingChannel.queueDeclare(qName, passive, durable, autoDelete, null)
  sendingChannel.queueBind(qName, exName, "*")
  sendingChannel.basicPublish("", qName, null, "Hello".getBytes())
}

object Consumer extends App {
  val config = ConfigFactory.load()
  val settings = new AMQPSettings(config.getConfig("akka.amqp.default"))
  val address = settings.addresses.map(com.rabbitmq.client.Address.parseAddress(_))
  val host = address.iterator.next.getHost()
  val port = address.iterator.next.getPort()

  val qConfig = config.getConfig("akka.amqp.splunk")

  val exName = qConfig.getString("exName")
  val passive = qConfig.getBoolean("passive")
  val exType = qConfig.getString("exType")

  val qName = qConfig.getString("qName")
  val durable = qConfig.getBoolean("durable")
  val exclusive = qConfig.getBoolean("exclusive")
  val autoDelete = qConfig.getBoolean("autoDelete")

  val connection = RabbitMQConnection.getConnection(host, port, settings)
  val consumingChannel = connection.createChannel()

  val consumer = new QueueingConsumer(consumingChannel)
  consumingChannel.basicConsume(qName, true, consumer)
  val delivery = consumer.nextDelivery()
  val msg = new String(delivery.getBody())
  println(msg)
}

case class AMQPSettings(config: Config) {
  val addresses: Seq[String] = config.getStringList("addresses").asScala.toSeq
  val user: String = config.getString("user")
  val pass: String = config.getString("pass")
  val vhost: String = config.getString("vhost")
  val amqpHeartbeat: FiniteDuration = DurationLong(config.getMilliseconds("heartbeat")).milli
  val maxReconnectDelay: Duration = DurationLong(config.getMilliseconds("max-reconnect-delay")).milli
  val channelThreads: Int = config.getInt("channel-threads")
  val interactionTimeout: Duration = DurationLong(config.getMilliseconds("interaction-timeout")).milli
  val channelCreationTimeout: Duration = DurationLong(config.getMilliseconds("channel-creation-timeout")).milli
  val channelReconnectTimeout: Duration = DurationLong(config.getMilliseconds("channel-reconnect-timeout")).milli
  val publisherConfirmTimeout: FiniteDuration = DurationLong(config.getMilliseconds("publisher-confirm-timeout")).milli
}

object RabbitMQConnection {
  private var connection: Connection = null

  def getConnection(host: String, port: Int, settings: AMQPSettings): Connection = {
    connection match {
      case null => {
        val factory = new ConnectionFactory()
        factory.setHost(host)
        factory.setPort(port)
        factory.setUsername(settings.user)
        factory.setPassword(settings.pass)
        factory.setVirtualHost(settings.vhost)
        factory.setRequestedHeartbeat(30)
        factory.setConnectionTimeout(60)
        connection = factory.newConnection()
        connection
      }
      case _ => connection
    }
  }
}