package com.iisrun.realtime.utils

import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.KafkaUtils

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/14 16:42
 * @Description: Kafka工具类
 */
object MyKafkaUtils {

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "hadoop61:9092,hadoop62:9092,hadoop63:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "bigdata",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (true: java.lang.Boolean)
  )

  def getKafkaStream(ssc: StreamingContext, topic: String) = {
    KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent, // 标配
      Subscribe[String, String](Set(topic), kafkaParams)
    ).map(_.value())
  }
}
