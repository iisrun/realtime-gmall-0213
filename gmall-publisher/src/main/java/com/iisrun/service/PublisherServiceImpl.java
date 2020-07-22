package com.iisrun.service;

import com.iisrun.mapper.DauMapper;
import com.iisrun.mapper.OrderMapper;
import com.iisrun.utils.EsUtil;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
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


    @Override
    public Map<String, Object> getSaleDetailAndAgg(String date, String keyword, int startPage, int sizePerPage, String aggField, int aggCount) throws IOException {
       /*
        "total":200
        "age":Map("M"->100,"F"->100),
         "detail":List(Map(一行记录，Map(...)))
         */
        String dsl = EsUtil.getDSl(date, keyword, aggField, aggCount, startPage, sizePerPage);
        // 获取ES客户端
        JestClient esClient = EsUtil.getESClient();
        // 创建查询对象
        Search search = new Search.Builder(dsl)
                .addIndex("gmall_sale_detail")
                .addType("_doc")
                .build();
        // 执行查询
        SearchResult searchResult = esClient.execute(search);
        // 解析查询结果
        Map<String, Object> result = new HashMap<>();
        // 1. 获取查询的总数
        Long total = searchResult.getTotal();
        result.put("total", total);
        // 2. 获取详情数据
        List<SearchResult.Hit<HashMap, Void>> hits = searchResult.getHits(HashMap.class);
        ArrayList<Map> detail = new ArrayList<>();
        for (SearchResult.Hit<HashMap, Void> hit : hits) {
            HashMap source = hit.source;
            detail.add(source);
        }
        result.put("detail", detail);
        // 3. 获取聚合结果
        HashMap<String, Long> agg = new HashMap<>();
        List<TermsAggregation.Entry> buckets = searchResult.getAggregations()
                .getTermsAggregation("group_by_" + aggField)
                .getBuckets();
        for (TermsAggregation.Entry bucket : buckets) {
            String key = bucket.getKey();
            Long value = bucket.getCount();
            agg.put(key, value);
        }
        result.put("agg", agg);
        // 把需要的数据封装到Map中返回
        return null;
    }
}
