package com.iisrun.utils

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}


/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/17 10:36
 * @Description: Kafka 生产者
 */
object MyKafkaSender {
  val props = new Properties()
  // Kafka服务端的主机名和端口号
  props.put("bootstrap.servers", "hadoop61:9092,hadoop62:9092,hadoop63:9092")
  // key序列化
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  // value序列化
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  val producer = new KafkaProducer[String, String](props)

  def sendToKafka(topic: String, content: String) = {
    producer.send(new ProducerRecord[String, String](topic, content))
  }

}
