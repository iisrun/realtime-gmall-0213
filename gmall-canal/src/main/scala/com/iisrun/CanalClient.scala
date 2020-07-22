package com.iisrun

import java.net.InetSocketAddress
import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.{time, util}

import com.alibaba.fastjson.JSONObject
import com.alibaba.otter.canal.client.{CanalConnector, CanalConnectors}
import com.alibaba.otter.canal.protocol.CanalEntry.{EntryType, EventType, RowChange}
import com.alibaba.otter.canal.protocol.{CanalEntry, Message}
import com.google.protobuf.ByteString
import com.iisrun.constants.GmallConstant

import scala.collection.JavaConversions._
import scala.util.Random

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/16 10:46
 * @Description:
 */
object CanalClient {

  def main(args: Array[String]): Unit = {
    // 1. 连接到 Canal
    // 1.1 创建能连接到 Canal 的连接器对象
    val address = new InetSocketAddress("hadoop61", 11111)
    val connector: CanalConnector = CanalConnectors.newSingleConnector( address,"example", "", "")
    connector.connect() // 连接到canal服务器

    // 2. 拉取数据
    connector.subscribe("gmall0213.*")
    while (true) {
      // 100表示最多拉取100条sql导致的变化数据
      // 所有的数据封装到一个Message中
      val msg: Message = connector.get(100)
      // 3. 解析数据
      val entries: java.util.List[CanalEntry.Entry] = msg.getEntries
      import scala.collection.JavaConversions._
      // 有可能本次拉取没有变化，需要做个判断
      if (entries.size() > 0) {
        for (entry <- entries) {
          // entry的类型必须是ROWDATA
          if (entry.getEntryType == EntryType.ROWDATA) {
            val value: ByteString = entry.getStoreValue
            val rowChange: RowChange = RowChange.parseFrom(value)
            // 所有行变化的数据
            val rowDatas: util.List[CanalEntry.RowData] = rowChange.getRowDatasList

            handleData(rowDatas, entry.getHeader.getTableName, rowChange.getEventType)
          }
        }
      } else {
        println(LocalDate.now() + " " + LocalTime.now() + " 没有抓取到数据...., 2s 之后重新抓取")
        Thread.sleep(2000)
      }
    }
  }

  /**
   * 处理从 canal 取来的数据
   *
   * @param tableName   表名
   * @param eventType   事件类型
   * @param rowDatas    行数据
   */
  def handleData(rowDatas: util.List[CanalEntry.RowData], tableName: String, eventType: CanalEntry.EventType) = {
    if ("order_info".equals(tableName) && eventType == EventType.INSERT && rowDatas.size() > 0) {
      sendToKafka(rowDatas, GmallConstant.ORDER_INFO_TOPIC)
    } else if ("order_detail".equals(tableName) && eventType == EventType.INSERT && rowDatas.size() > 0) {
      sendToKafka(rowDatas, GmallConstant.ORDER_DETAIL_TOPIC)
    }
  }
  // 抽取功能方法ctrl+alt+m
  private def sendToKafka(rowDatas: util.List[CanalEntry.RowData], topic: String): Unit = {
    // rowData 表示一行数据, 通过他得到每一列. 首先遍历每一行数据
    for (rowData <- rowDatas) {
      // 得到每行中, 所有列组成的列表
      // rowData.getAfterXXXX   // 变化后的数据
      // rowData.getBeforeXXXX  // 变化前的数据

      // mysql中的一行，到kafka的时候是一条(转换成json)
      val obj: JSONObject = new JSONObject()
      val columnList: util.List[CanalEntry.Column] = rowData.getAfterColumnsList
      for (column <- columnList) {
        // id:100 total_amount:1000.2
        val key: String = column.getName
        val value: String = column.getValue
        obj.put(key, value)
      }
      println("sendToKafka:" + obj.toString)

      // 使用子线程来模拟随机延迟来模拟网络延迟
      new Thread() {
        override def run() = {
          Thread.sleep(new Random().nextInt(5 * 1000))
          MyKafkaUtils.sendToKafka(topic, obj.toJSONString)
        }
      }.start()
      // 发送到 Kafka 组成json字符串，写入到kafka
//      MyKafkaUtils.sendToKafka(topic, obj.toJSONString)
    }
  }

}
