package com.iisrun.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: drz
 * @Date: 2020/07/22 11:10
 * @Description:
 */
public class Stat {
    private String title;
    private List<Option> options = new ArrayList<>();

    public Stat() {
    }

    public Stat(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void addOption(Option opt,Option ...others) {
        this.options.add(opt);
        for (Option other : others) {
            this.options.add(other);
        }
    }
}
