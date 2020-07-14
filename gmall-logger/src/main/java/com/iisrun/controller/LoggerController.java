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
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/log")
    public String doLog(@RequestParam("log")String log){

        //创建JSON对象
        JSONObject logObj = JSON.parseObject(log);
        // 1. 给日志添加一个时间戳
        logObj = addTS(logObj);
        // 2. 数据落盘(为离线数据做准备)
        // 日志落盘
        saveLog(logObj);
        // 3. 把数据写入到kafka，需要写入到topic
        //根据数据中的"type"字段选择发送至不同的主题
        if ("startup".equals(logObj.getString("type"))) {
            kafkaTemplate.send(GmallConstant.GMALL_STARTUP, logObj.toString());
        } else {
            kafkaTemplate.send(GmallConstant.GMALL_EVENT, logObj.toString());
        }
        return "success";

    }

    /**
     * 添加时间戳
     * @param logObj
     * @return
     */
    public JSONObject addTS(JSONObject logObj){
        logObj.put("ts", System.currentTimeMillis());
        return logObj;
    }
    /**
     * 日志落盘
     * 使用 log4j
     * @param logObj
     */
    public void saveLog(JSONObject logObj) {
        logger.info(logObj.toJSONString());
    }


}
