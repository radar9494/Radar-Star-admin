package com.liuqi.business.service;

import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.dto.PriceNumsDto;
import com.liuqi.business.dto.RecordDto;
import com.liuqi.business.model.TradeRecordModelDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 交易相关数据
 */
public interface TradeInfoCacheService {

    /**
     * 是否撮合
     * @param tradeId
     * @return
     */
    boolean canTrade(Long tradeId);
    /**
     * 发布修改缓存
     * @param tradeId
     * @param tradeType
     * @param price
     * @param incrementNum
     * @param isRobot
     */
    void publishCache(Long tradeId,Integer tradeType, BigDecimal price, BigDecimal incrementNum,boolean isRobot);

    /**
     * 取消修改缓存
     * @param trusteeId
     */
     void cancelCache(Long trusteeId);

    /**
     * 交易修改缓存
     *
     * @param record
     */
    void tradeRecordCache(TradeRecordModelDto record);


    /**
     * 定期同步缓存
     * @param tradeId
     */
    void syncInfo(Long tradeId);


    /**
     * 买单
     * @param tradeId
     * @return
     */
    List<PriceNumsDto> buyList(Long tradeId);

    /**
     * 卖单
     * @param tradeId
     * @return
     */
    List<PriceNumsDto> sellList(Long tradeId) ;

    /**
     * 获取交易记录
     *
     * @param tradeId
     * @return
     */
    List<RecordDto> tradeRecordList(Long tradeId);

    /**
     * 获取交易信息
     *
     * @param tradeId
     * @return
     */
    JSONObject getTradeInfo(Long tradeId);


    /**
     * 深度图
     *
     * @param tradeId
     * @return
     */
    JSONObject getTradeDepthInfo(Long tradeId,Integer gear);


}
