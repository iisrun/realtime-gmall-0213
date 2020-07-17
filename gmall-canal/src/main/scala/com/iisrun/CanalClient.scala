package com.iisrun

import java.net.InetSocketAddress
import java.util

import com.alibaba.otter.canal.client.{CanalConnector, CanalConnectors}
import com.alibaba.otter.canal.protocol.CanalEntry.{EntryType, RowChange}
import com.alibaba.otter.canal.protocol.{CanalEntry, Message}
import com.google.protobuf.ByteString
import com.iisrun.utils.CanalHandler

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

            CanalHandler.handle(entry.getHeader.getTableName, rowChange.getEventType, rowDatas)
          }
        }
      } else {
        println("没有抓取到数据...., 2s 之后重新抓取")
        Thread.sleep(2000)
      }
    }
  }

}
