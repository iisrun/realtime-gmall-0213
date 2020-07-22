package com.iisrun.service;

import java.io.IOException;
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

    /**
     * 从es读取数据，返回需要的数据到Controller
     *
     * @param date 时间
     * @param keyword 搜索的关键字
     * @param startPage 页数
     * @param sizePerPage 每页几条
     * @param aggField 聚合的字段
     * @param aggCount 聚合后的条数
     * @return
     */
    Map<String,Object> getSaleDetailAndAgg(String date,
                               String keyword,
                               int startPage,
                               int sizePerPage,
                               String aggField,
                               int aggCount) throws IOException;
}
