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
  private val jedisPoolConfig = new JedisPoolConfig()
  jedisPoolConfig.setMaxTotal(100) //最大连接数
  jedisPoolConfig.setMaxIdle(20) //最大空闲
  jedisPoolConfig.setMinIdle(20) //最小空闲
  jedisPoolConfig.setBlockWhenExhausted(true) //忙碌时是否等待
  jedisPoolConfig.setMaxWaitMillis(10000) //忙碌时等待时长 毫秒
  jedisPoolConfig.setTestOnCreate(true)   //创建的时候测试
  jedisPoolConfig.setTestOnBorrow(true)   //每次获得连接的进行测试
  jedisPoolConfig.setTestOnReturn(true)
  private val jedisPool: JedisPool = new JedisPool(jedisPoolConfig, host, port)

  // 直接得到一个 Redis 的连接
  def getJedisClient: Jedis = {
//    jedisPool.getResource
    // 使用上面的方式会报错，优化为下面的方式
    // redis.clients.jedis.exceptions.JedisConnectionException: Could not get a resource from the pool
    new Jedis("hadoop61",6379)
  }

  def main(args: Array[String]): Unit = {
    val client :Jedis = getJedisClient
    client.set("k1","redis")

  }

}
