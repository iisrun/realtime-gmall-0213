package com.iisrun.realtime.app

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/17 14:16
 * @Description:
 */
trait BaseApp {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("DauApp")
    val ssc = new StreamingContext(conf, Seconds(3))

    run(ssc)

    ssc.start()
    ssc.awaitTermination()
  }

  def run(ssc: StreamingContext): Unit
}
