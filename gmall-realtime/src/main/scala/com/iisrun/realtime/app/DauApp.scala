package com.iisrun.realtime.app

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.alibaba.fastjson.JSON
import com.iisrun.constants.GmallConstant
import com.iisrun.realtime.bean.StartupLog
import com.iisrun.realtime.utils.{MyKafkaUtils, RedisUtils}
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/14 16:38
 * @Description: 使用SparkStreaming实时从kafka消费数据并进行过滤
 */
object DauApp {
  def main(args: Array[String]): Unit = {

    // 1. 创建一个StreamingContext
    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("DauApp")
    val ssc = new StreamingContext(conf,Seconds(3))

    // 2. 获取一个流
    val sourceStream: DStream[String] = MyKafkaUtils.getKafkaStream(ssc,GmallConstant.STARTUP_TOPIC)
    // 2.1 把每个json字符串的数据，封装到一个样例类对象中
    val startupLogStream: DStream[StartupLog] = sourceStream.map(json => JSON.parseObject(json,classOf[StartupLog]))
    startupLogStream.print(1000)

    // 3. 去重 过滤掉已经启动的那些设备的记录，从redis去读取已经启动过的设备id
    // transform():transformFunc: (RDD[T], Time) => RDD[U] 按批次执行，也是三秒执行一次，和上面的执行时间一致
    // 为什么不用filter()过滤数据？如果用filter过滤的话每条日志都会访问redis，会导致连接数过多。
    val filteredStartupLogStream: DStream[StartupLog] = startupLogStream.transform(rdd => {
      // 3.1 先去读redis的数据
      val client: Jedis = RedisUtils.getClient
      val mids: util.Set[String] = client.smembers(GmallConstant.STARTUP_TOPIC + ":" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
      // 归还连接池
      client.close()
      // 3.2 把集合做一个广播变量
      val midsBD: Broadcast[util.Set[String]] = ssc.sparkContext.broadcast(mids)
      // 返回那些没有启动过的设备的启动记录
      rdd.filter(startupLog => !midsBD.value.contains(startupLog.mid))
        // 如果一个设备在他第一次启动的批次中有多次启动记录，则无法过滤
        .map(log => (log.mid, log))
        .groupByKey()
        .map {
          case (_, logs) => {
            logs.toList.sortBy(_.ts).head
          }
        }
    })

    // 3.3 把第一次启动的记录写入到redis中
    filteredStartupLogStream.foreachRDD(rdd => {
      // rdd的数据写入到redis，只需要写mid就行了
      // 一个分区一个分区的写
      rdd.foreachPartition(logs => {
        val client: Jedis = RedisUtils.getClient
        logs.foreach(log => {
          client.sadd(GmallConstant.STARTUP_TOPIC + ":" + log.logDate, log.mid)
        })
        client.close()
      })
      // 4. 数据写入到HBase中，当天启动的设备的第一条记录
      import org.apache.phoenix.spark._
      rdd.saveToPhoenix(
        "GMALL_DAU",
        Seq("MID", "UID", "APPID", "AREA", "OS", "CHANNEL", "LOGTYPE", "VERSION", "TS", "LOGDATE", "LOGHOUR"),
        zkUrl = Some("hadoop61,hadoop62,hadoop63:2181")
      )
    })
    filteredStartupLogStream.print(10000)

    // 6. 开启流
    ssc.start()

    // 6.阻止main退出
    ssc.awaitTermination()
  }
}
