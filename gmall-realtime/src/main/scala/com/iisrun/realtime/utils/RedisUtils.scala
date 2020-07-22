package com.iisrun.realtime.utils

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/14 18:27
 * @Description: Redis工具类
 */
object RedisUtils {

  val host = ConfigUtils.getProperty("redis.host")
  val port = ConfigUtils.getProperty("redis.port").toInt
  private val conf = new JedisPoolConfig()
  conf.setMaxTotal(100)             //最大连接数
  conf.setMaxIdle(20)               //最大空闲
  conf.setMinIdle(20)               //最小空闲
  conf.setBlockWhenExhausted(true)  //忙碌时是否等待
  conf.setMaxWaitMillis(10000)      //忙碌时等待时长 毫秒
  conf.setTestOnCreate(true)        //创建的时候测试
  conf.setTestOnBorrow(true)        //每次获得连接的进行测试
  conf.setTestOnReturn(true)

//  private val jedisPool: JedisPool = new JedisPool(conf, host, port)

  // 直接得到一个 Redis 的连接
  def getClient: Jedis = {
//    jedisPool.getResource
    // 使用上面的方式会报错，优化为下面的方式
    // redis.clients.jedis.exceptions.JedisConnectionException: Could not get a resource from the pool
    new Jedis("hadoop61",6379)
  }

  def main(args: Array[String]): Unit = {
    val client :Jedis = getClient
    client.set("k1","redis")
    client.close()
  }

}
