package com.iisrun

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/17 10:36
 * @Description: Kafka 生产者
 */
object MyKafkaUtils {
  val pops = new Properties()
  // Kafka服务端的主机名和端口号
  pops.put("bootstrap.servers", "hadoop61:9092,hadoop62:9092,hadoop63:9092")
  pops.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  pops.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](pops)

  def sendToKafka(topic: String, content: String) = {
    producer.send(new ProducerRecord[String, String](topic, content))
  }

}
