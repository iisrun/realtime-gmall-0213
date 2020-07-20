package com.iisrun.realtime.app
import java.util

import com.alibaba.fastjson.JSON
import com.iisrun.constants.GmallConstant
import com.iisrun.realtime.bean.{AlertInfo, EventLog}
import com.iisrun.realtime.utils.MyKafkaUtils
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.DStream

import scala.util.control.Breaks._

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/18 8:52
 * @Description:
 */
object AlertApp extends BaseApp {
  // json4s专门给scala 序列号工具
  // fastjson 在scala可能遇到问题
  override def run(ssc: StreamingContext): Unit = {
    val eventLogStream: DStream[EventLog] = MyKafkaUtils.getKafkaStream(ssc, GmallConstant.EVENT_TOPIC)
      .map(log => JSON.parseObject(log, classOf[EventLog]))
      .window(Minutes(5), Seconds(6)) // 给流添加窗口

    // 1. 按照设备id进行分组
    val eventLogGroupStream: DStream[(String, Iterable[EventLog])] = eventLogStream
      .map(event => (event.mid, event))
      .groupByKey
    // 2. 产生预警信息
    val alertInfoStream: DStream[(Boolean, AlertInfo)] = eventLogGroupStream.map {
      case (mid, eventLogIt) => {
        // eventLogIt 表示当前mid上5分钟内所有的事件
        val uidSet = new util.HashSet[String]()
        // 存储5分组内在当前设备上所有的事件
        val eventList = new util.ArrayList[String]()
        // 存储优惠券对应的那些商品id
        val itemSet = new util.HashSet[String]()
        // 表示是否点击过商品
        var isClickItem = false
        breakable {
          eventLogIt.foreach(log => {
            // 把事件id添加到eventList
            eventList.add(log.eventId)
            // 只关注领取优惠券的用户
            log.eventId match {
              case "coupon" =>
                uidSet.add(log.uid) // 领取优惠券的用户
                itemSet.add(log.itemId) // 优惠券对应的商品
              case "clickItem" =>
                // 一旦出现浏览商品，则不会再产生预警信息
                isClickItem = true
                break
              case _ => // 其他时间不做任何处理
            }
          })
        }

        // 是否预警，alert
        (!isClickItem && uidSet.size() >= 3, AlertInfo(mid, uidSet, itemSet, eventList, System.currentTimeMillis()))
      }
    }

    // 3. 把数据写入到es中

    alertInfoStream.print(1000)

  }
}
/*
   需求：同一设备，5分钟内三次及以上用不同账号登录并领取优惠劵，
   并且在登录到领劵过程中没有浏览商品。同时达到以上要求则产生一条预警日志。
   同一设备，每分钟只记录一次预警。
   --------------
   需求分析：
     同一个设备 -> group by mid_id
     5分钟内的数据，每6秒统计一次  -> 窗口 窗口的长度：5分钟，步长：6s

     三次及以上用不同账号登录 -> 统计每个设备的登录用户数
     领取优惠券 -> 统计领取优惠券的行为

     并且在登陆领取过程没有浏览商品 -> 事件中没有流量商品行为
     同一设备，每分钟只记录一次预警。-> 不在Spark-Streaming完成，让es来完成
    ----
   // 1. reduceByKeyAndWindow
      2. 直接在流上使用window, 将来所有的操作都是基于这个窗口
   */