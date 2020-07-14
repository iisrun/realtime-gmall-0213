package com.iisrun.realtime.app

import java.text.SimpleDateFormat
import java.util.Date

import com.alibaba.fastjson.JSON
import com.iisrun.constants.GmallConstant
import com.iisrun.realtime.bean.StartupLog
import com.iisrun.realtime.utils.{MyKafkaUtils, RedisUtils}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/14 16:38
 * @Description:
 */
object DauApp {
  def main(args: Array[String]): Unit = {

    // 1. 创建一个StreamingContext
    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("DauApp")
    val ssc = new StreamingContext(conf,Seconds(3))

    // 2. 获取一个流
    val sourceStream: DStream[String] = MyKafkaUtils.getKafkaStream(ssc,GmallConstant.STARTUP_TOPIC)
    sourceStream.print(1000)

    // 3. 去重

    // 4. 数据写入到HBase

    // 6. 开启流
    ssc.start()

    // 6.阻止main退出
    ssc.awaitTermination()
  }
}
