package com.iisrun.utils

import io.searchbox.client.{JestClientFactory}
import io.searchbox.client.config.HttpClientConfig



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

  def getESClient() = factory.getObject

  def getDSL(date: String, keyword: String, aggField: String, aggSize: Int, startPage: Int, sizePerPage: Int) = {
    s"""
       |"${date}"
       |""".stripMargin
    ""
    //TODO 未完成
  }
}
