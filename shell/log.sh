#!/bin/bash
# 群起日志服务器
case $1 in
"start")
    for host in hadoop61 hadoop62 hadoop63 ; do
        echo "========启动日志服务: $host==============="
        ssh $host "source /etc/profile ; nohup java -jar /opt/project/gmall-logger-0.0.1-SNAPSHOT.jar 1>/dev/null 2>&1 &"
    done
    ;;
"stop")
    for host in hadoop61 hadoop62 hadoop63 ; do
        echo "========关闭日志服务: $host==============="
        #ssh $host "source /etc/profile ; jps | grep gmall-logger-0.0.1-SNAPSHOT.jar | awk '{print \$1}' | xargs kill -9"
        ssh $host "ps -ef|grep gmall-logger-0.0.1-SNAPSHOT.jar | grep -v grep|awk '{print \$2}'|xargs kill" >/dev/null 2>&1
    done
    ;;
*)
    echo "启动姿势不对："
    echo "  log.sh start 启动日志采集服务器"
    echo "  log.sh stop  停止日志采集服务器"
    ;;
esac
