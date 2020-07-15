package com.iisrun.realtime.utils

import java.io.InputStream
import java.util.Properties


/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/14 18:24
 * @Description: 配置文件Util
 */
object ConfigUtils {
  private val is: InputStream = ClassLoader.getSystemResourceAsStream("config.properties")
  private val properties = new Properties()
  properties.load(is)

  def getProperty(propertyName: String): String = properties.getProperty(propertyName)

  def main(args: Array[String]): Unit = {
    println(getProperty("kafka.group.id"))
  }

}
