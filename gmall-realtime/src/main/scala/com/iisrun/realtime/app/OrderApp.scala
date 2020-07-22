package com.iisrun.realtime.app
import com.alibaba.fastjson.JSON
import com.iisrun.constants.GmallConstant
import com.iisrun.realtime.bean.OrderInfo
import com.iisrun.realtime.utils.MyKafkaUtils
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.phoenix.spark._

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/17 14:15
 * @Description:
 */
object OrderApp extends BaseApp {
  override def run(ssc: StreamingContext): Unit = {
    val orerInfoDStream = MyKafkaUtils.getKafkaStream(ssc, GmallConstant.ORDER_INFO_TOPIC)
      .map(json => JSON.parseObject(json, classOf[OrderInfo]))

    orerInfoDStream.foreachRDD(rdd => {
      import org.apache.phoenix.spark._
      rdd saveToPhoenix(
        "GMALL_ORDER_INFO",
        Seq("ID", "PROVINCE_ID", "CONSIGNEE", "ORDER_COMMENT", "CONSIGNEE_TEL", "ORDER_STATUS", "PAYMENT_WAY", "USER_ID", "IMG_URL", "TOTAL_AMOUNT", "EXPIRE_TIME", "DELIVERY_ADDRESS", "CREATE_TIME", "OPERATE_TIME", "TRACKING_NO", "PARENT_ORDER_ID", "OUT_TRADE_NO", "TRADE_BODY", "CREATE_DATE", "CREATE_HOUR"),
        zkUrl = Some("hadoop61,hadoop62,hadoop63:2181"))
    })
    orerInfoDStream.print

  }
}
