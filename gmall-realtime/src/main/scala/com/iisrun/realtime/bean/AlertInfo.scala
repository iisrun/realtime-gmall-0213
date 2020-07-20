package com.iisrun.realtime.bean

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/18 9:41
 * @Description:
 */
case class AlertInfo(mid: String,
                     uids: java.util.HashSet[String],
                     itemIds: java.util.HashSet[String],
                     events: java.util.List[String],
                     ts: Long)
