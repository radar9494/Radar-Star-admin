package com.liuqi.business.service;

import com.github.pagehelper.PageInfo;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.RecordDto;
import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.model.KDataModelDto;
import com.liuqi.business.model.TradeRecordModel;
import com.liuqi.business.model.TradeRecordModelDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface TradeRecordService extends BaseService<TradeRecordModel, TradeRecordModelDto> {
    /**
     * 查询开盘价
     *
     * @param tradeId
     * @return
     */
    BigDecimal getDayOpenPrice(Long tradeId);
    /**
     * 获取关盘价格
     *
     * @param startTime
     * @param tradeId
     * @return
     */
    BigDecimal getOpenPriceByDate(Date startTime, Long tradeId);
    /**
     * 获取关盘价格
     *
     * @param endtime
     * @param tradeId
     * @return
     */
    BigDecimal getClosePriceByDate(Date endtime, Long tradeId);
    /**
     * 查询交易对今天信息
     *
     * @param tradeId
     * @return
     */
    TradeInfoDto getTodayTrade(Long tradeId);
    /**
     * 查询交易对当前价格
     *
     * @param tradeId
     * @return
     */
    BigDecimal getCurrentTradePrice(Long tradeId);

    /**
     * 获取时间内的最大价格，最小价格，成交数量
     *
     * @param startDate
     * @param endDate
     * @param tradeId
     * @return
     */
    KDataModelDto getTradeDataByDate(Date startDate, Date endDate, Long tradeId);


    /**
     * 查询最新多少条交易对交易
     * @param tradeId
     * @param num
     * @return
     */
    List<TradeRecordModelDto> findTradeRecordList(Long tradeId, int num);

    /**
     * 查询交易记录
     * @param tradeId
     * @param num
     * @return
     */
    List<RecordDto> findRecordList(Long tradeId, int num);

}
