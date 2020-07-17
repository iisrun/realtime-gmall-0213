package com.iisrun.mapper;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/17 15:00
 * @Description:
 */
public interface OrderMapper {
    /**
     * 当日销售总额
     * @param date
     * @return
     */
    Double getTotalAmount(String date);

    /**
     * 分时销售额
     * @param date
     * @return
     */
    List<Map<String,Object>> getHourAmount(String date);
}
