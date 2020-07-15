package com.iisrun.controller;

import com.alibaba.fastjson.JSON;
import com.iisrun.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/15 14:55
 * @Description:
 */
@RestController
public class PublisherController {
    @Autowired
    PublisherService service;

    @GetMapping("/realtime-total")
    public String realtimeTotal(@RequestParam("date") String date) {

        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        Map<String, String> map1 = new HashMap<>();
        map1.put("id", "dau");
        map1.put("name", "新增日活");
        map1.put("value", "dau");
        result.add(map1);

        Map<String, String> map2 = new HashMap<>();
        map2.put("id", "new_mid");
        map2.put("name", "新增设备");
        map2.put("value", "223");
        result.add(map2);

        return JSON.toJSONString(result);
    }

    @GetMapping("/realtime-hour")
    public String getRealtimeHour(String id, String date) {
        if ("dau".equals(id)) {
            Map<String, Long> today = service.getHourDau(date);
            Map<String, Long> yesterday = service.getHourDau(getYesterDay(date));

            HashMap<String, Map<String, Long>> result = new HashMap<>();
            result.put("today", today);
            result.put("yesterday", yesterday);

            return JSON.toJSONString(result);
        }
        return null;
    }

    /**
     * 返回昨天的年月日
     * @param date
     * @return
     */
    private String getYesterDay(String date){
        return LocalDate.parse(date).plusDays(-1).toString();
    }
}
