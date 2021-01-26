package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TradeInfoDto implements Serializable{

    /**
     * 今日交易的最高价格
     */
    private BigDecimal tradeMaxPrice;
    /**
     * 今日交易的最低价格
     */
    private BigDecimal tradeMinPrice;
    /**
     * 成交量
     */
    private BigDecimal tradeNums;
    /**
     * 当前成交价格
     */
    private BigDecimal currentPrice;
    //币种名称
    private Long  currencyId;
    //币种名称
    private Long  tradeCurrencyId;
    //币种名称
    private String  currencyName;
    //交易币种名称
    private String tradeCurrencyName;
    private Long tradeId;//交易对id
    //涨跌幅
    private BigDecimal rise;
    //是否开启
    private Boolean usings;
    //成交金额
    private BigDecimal tradeMoney;

    private Long areaId;
    /**
     * 区 1主创区 2原创区
     */
    private Integer area;
    //开盘价
    private BigDecimal open;
    //查询时间
    private String recordTime;

    //第一次查询类型 0数据库 1缓存
    private Integer searchType;

    //交易对基础价格
    private Long baseId;
    private BigDecimal basePrice;
    private String basePriceName;

    //usdt价格
    private BigDecimal usdtPirce;
}
