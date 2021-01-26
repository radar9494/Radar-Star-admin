package com.liuqi.business.dto;

import groovy.beans.Bindable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ClassName WalletStat
 * @Description TODO
 * @Author shi huang qin
 * @Date 2020/7/19 16:31
 * @Version 2020 Ultimate Version
 */
public class WalletStat {


    /**
     * name : 邮件营销
     * data : [120,132,101,134,90,230,210]
     */

    private String name;
    private List<BigDecimal> data;
    private String type;

    public List<BigDecimal> getData() {
        return data;
    }

    public void setData(List<BigDecimal> data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
