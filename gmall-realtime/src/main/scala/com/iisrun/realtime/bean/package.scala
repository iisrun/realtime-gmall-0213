package com.iisrun.realtime

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/14 18:42
 * @Description:
 */
package object bean {
  case class StartupLog(mid: String,
                        uid: String,
                        appId: String,
                        area: String,
                        os: String,
                        channel: String,
                        logType: String,
                        version: String,
                        ts: Long,
                        var logDate: String,
                        var logHour: String)
}
