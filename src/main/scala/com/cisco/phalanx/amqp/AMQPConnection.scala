package com.cisco.phalanx.amqp

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

object AMQPConnectionConfig {
  val RABBITMQ_HOST = "localhost"
  val RABBITMQ_QUEUE = "splunkQueue"
  val RABBITMQ_EXCHANGE = "splunkExchange"
}

object AMQPConnection {
  private val connection: Connection = null

  /**
   * Return a connection if one doesn't exist. Else create
   * a new one
   */
  def getConnection(): Connection = {
    connection match {
      case null => {
        val factory = new ConnectionFactory();
        factory.setHost(AMQPConnectionConfig.RABBITMQ_HOST);
        factory.newConnection();
      }
      case _ => connection
    }
  }
}
