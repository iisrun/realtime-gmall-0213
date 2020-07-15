package com.iisrun.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/15 14:52
 * @Description:
 */
@Service
public interface PublisherService {

    /**
     * 获取总的日活
     * @param date
     * @return
     */
    Long getDau(String date);

    /**
     * 日活分时统计
     * @param date
     * @return
     */
    Map<String,Long> getHourDau(String date);
}
