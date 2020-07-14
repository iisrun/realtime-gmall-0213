package com.iisrun.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iisrun.constants.GmallConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/13 16:46
 * @Description:
 */
@RestController
public class LoggerController {
    // 初始化 Logger 对象
    private final Logger logger = LoggerFactory.getLogger(LoggerController.class);
    @Autowired
    private KafkaTemplate kafka;

    @PostMapping("/log")
    public String doLog(@RequestParam("log")String log){
        // 1. 给日志添加一个时间戳
        log = addTS(log);
        // 2. 数据落盘(为离线数据做准备)
        // 日志落盘
        saveToDisk(log);
        // 3. 把数据写入到kafka，需要写入到topic
        sendToKafka(log);
        System.out.println(log);
        return "success";
    }

    /**
     * 添加时间戳
     * @param log
     * @return
     */
    public String addTS(String log){
        JSONObject obj = JSON.parseObject(log);
        obj.put("ts",System.currentTimeMillis());
        return JSON.toJSONString(obj);
    }

    /**
     * 将日志写入到磁盘
     * 使用 log4j
     * @param log
     */
    public void saveToDisk(String log) {
        logger.info(log);
    }

    /**
     *  把日志发送到kafka
     *  不同的日志写入到不同的topic
     * @param log
     */
    private void sendToKafka(String log) {
        if (log.contains("\"startup\"")) {
            kafka.send(GmallConstant.STARTUP_TOPIC, log);
        } else {
            kafka.send(GmallConstant.EVENT_TOPIC, log);
        }

    }
}
