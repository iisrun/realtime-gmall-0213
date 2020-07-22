package com.iisrun.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/22 11:15
 * @Description:
 */
public class SaleInfo {
    private Long total;
    private List<Stat> stats = new ArrayList<>();
    private List<Map> detail;

    public SaleInfo() {
    }

    public SaleInfo(Long total, List<Map> detail) {
        this.total = total;
        this.detail = detail;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<Stat> getStats() {
        return stats;
    }

    public void addStats(Stat stat,Stat ... other) {
        this.stats.add(stat);
        Collections.addAll(this.stats,other);
    }

    public List<Map> getDetail() {
        return detail;
    }

    public void setDetail(List<Map> detail) {
        this.detail = detail;
    }
}
