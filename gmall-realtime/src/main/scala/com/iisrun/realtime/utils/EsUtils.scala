package com.iisrun.realtime.utils

import com.iisrun.realtime.bean.AlertInfo
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.client.config.HttpClientConfig
import io.searchbox.core.{Bulk, Index}
import org.apache.spark.rdd.RDD



/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/20 14:07
 * @Description:
 */
object EsUtils {
  val esUrl: String = "http://hadoop61:9200"
  val factory = new JestClientFactory
  val conf = new HttpClientConfig.Builder(esUrl)
    .connTimeout(1000 * 10)              // 连接超时时间
    .readTimeout(1000 * 10)              // 读取服务器资源超时时间
    .maxTotalConnection(100)      // HttpClient连接池的最大连接数
    .multiThreaded(true)              // 开启多线程
    .build()
  factory.setHttpClientConfig(conf)

  def main(args: Array[String]): Unit = {
    // 单条插入测试
    insertSingle("user",User("yy",100))

    // 批量有id插入测试
    val it1 = List((1, User("cc", 1)), (2, User("bb", 2)), (3, User("dd", 4))).toIterator
    // 批量无id插入测试
    val it2 = List(User("cc22", 1), User("dd2", 2)).toIterator
    insertBulk("user",it2)
  }

  /**
   * 批量插入(兼用有id和无id插入<br />
   * 使用模式匹配来进行区分
   *
   * @param index
   * @param source
   */
  def insertBulk(index: String, source: Iterator[Object]) = {
    val client: JestClient = factory.getObject

    val bulkBuilder = new Bulk.Builder()
      .defaultIndex(index)
      .defaultType("_doc")

    source.foreach {
      case (id: String, source) =>
        val builder = new Index.Builder(source).id(id)
        bulkBuilder.addAction(builder.build())
      case source =>
        val builder = new Index.Builder(source)
        bulkBuilder.addAction(builder.build())
    }
    client.execute(bulkBuilder.build())
    client.shutdownClient()
  }



  /**
   * 插入单条数据
   * @param index
   * @param source
   * @param id
   */
  def insertSingle(index: String, source: Object, id: String = null) = {
    val client: JestClient = factory.getObject
    val builder = new Index.Builder(source)
      .index(index)
      .`type`("_doc")
      .id(id)
      .build()
    client.execute(builder)
    client.shutdownClient()
  }

  /**
   * 隐式类
   * @param rdd
   */
  implicit class RichES(rdd: RDD[AlertInfo]) {
    def saveToES(index: String) = {
      rdd.foreachPartition((it: Iterator[AlertInfo]) => {
        // es 每个document都有id，id如果使用分钟表示：一分钟之内的数据就只会有一条数据
        // 为了防止设备2把设备1的记录覆盖，这里拼接上设备id
        EsUtils.insertBulk(index, it.map(info => (info.mid + ":" + info.ts / 1000 / 60, info)))
      })
    }
  }

}

case class User(name:String,age:Long)
