package com.iisrun.service;

import com.iisrun.mapper.DauMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private DauMapper dau;

    @Override
    public Long getDau(String date) {
        return dau.getDau(date);
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
}
