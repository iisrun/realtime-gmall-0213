package com.iisrun.service;

import com.iisrun.mapper.DauMapper;
import com.iisrun.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/15 14:53
 * @Description:
 */
@Service
public class PublisherServiceImpl implements PublisherService{

    @Resource
    private DauMapper dau;

    @Resource
    private OrderMapper order;

    @Override
    public Long getDau(String date) {
        Long dau = this.dau.getDau(date);
        return dau == null ? 0 : dau;
    }

    @Override
    public Map<String,Long> getHourDau(String date) {
        List<Map<String, Object>> hourDau = dau.getHourDau(date);
        HashMap<String, Long> result = new HashMap<>();
        for (Map<String, Object> map : hourDau) {
            String key = map.get("LOGHOUR").toString();
            Long value = (Long) map.get("COUNT");
            result.put(key,value);
        }
        return result;
    }

    @Override
    public Double getTotalAmount(String date) {
        Double totalAmout = order.getTotalAmount(date);
//        Double totalAmout = 222D;
        return totalAmout == null ? 0 : totalAmout;
    }

    @Override
    public Map<String, Double> getHourAmount(String date) {
        List<Map<String, Object>> hourAmountList = order.getHourAmount(date);
        Map<String, Object> resultMap = new HashMap<>();
        for (Map<String, Object> map : hourAmountList) {
            String key = (String) map.get("CREATE_HOUR");
            Double value = ((BigDecimal) map.get("SUM")).doubleValue();
            resultMap.put(key, value);
        }
        return null;
    }
}
