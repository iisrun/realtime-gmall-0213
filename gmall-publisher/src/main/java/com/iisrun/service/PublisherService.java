package com.iisrun.service;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/15 14:52
 * @Description:
 */
public interface PublisherService {

    /**
     * 获取总的日活
     *
     * @param date
     * @return
     */
    Long getDau(String date);

    /**
     * 日活分时统计
     *
     * @param date
     * @return
     */
    Map<String, Long> getHourDau(String date);

    /**
     * 当日销售总额
     *
     * @param date
     * @return
     */
    Double getTotalAmount(String date);

    /**
     * 获取小时的销售额
     *
     * @param date
     * @return
     */
    Map<String, Double> getHourAmount(String date);
}
