package com.iisrun.mapper;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/15 14:29
 * @Description:
 */
public interface DauMapper {

    /**
     * 得到当日日活
     * @param date
     * @return
     */
    Long getDau(String date);

    /**
     * 日活分时统计
     * @param date
     * @return
     */
    List<Map<String,Object>> getHourDau(String date);
}
