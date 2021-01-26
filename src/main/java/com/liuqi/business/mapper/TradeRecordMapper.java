package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.dto.RecordDto;
import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.model.KDataModelDto;
import com.liuqi.business.model.TradeRecordModel;
import com.liuqi.business.model.TradeRecordModelDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


public interface TradeRecordMapper extends BaseMapper<TradeRecordModel, TradeRecordModelDto> {
    /**
     * 获取关盘价格
     *
     * @param endTime
     * @return
     */
    BigDecimal findClosePriceByDate(@Param("endTime") Date endTime, @Param("tradeId") Long tradeId);

    BigDecimal findOpenPriceByDate(@Param("startTime") Date startTime, @Param("tradeId") Long tradeId);
    /**
     * 查询开盘价
     *
     * @param startDate
     * @param tradeId
     * @return
             */
    BigDecimal selectDayOpenPrice(@Param("startDate") Date startDate, @Param("tradeId") Long tradeId);

    /**
     * 查询今天第一笔交易
     *
     * @param startDate
     * @param tradeId
     * @return
     */
    BigDecimal selectDayFirstPrice(@Param("startDate") Date startDate, @Param("tradeId") Long tradeId);

    /**
     * 查询交易对今天信息
     *
     * @param tradeId
     * @return
     */
    TradeInfoDto findTodayTrade(Long tradeId);

    /**
     * 查询交易对当前价格
     *
     * @param tradeId
     * @return
     */
    BigDecimal selectCurrentTradePrice(Long tradeId);

    /**
     * 获取时间内的最大价格，最小价格，成交数量
     *
     * @param startDate
     * @param endDate
     * @return
     */
    KDataModelDto findTradeDataByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("tradeId") Long tradeId);

    /**
     * 查询最新多少条交易对交易
     * @param tradeId
     * @param num
     * @return
     */
    List<TradeRecordModelDto> findTradeRecordList(@Param("tradeId")Long tradeId, @Param("num")int num);
    /**
     * 查询最新多少条交易对交易
     * @param tradeId
     * @param num
     * @return
     */
    List<RecordDto> findRecordList(@Param("tradeId")Long tradeId, @Param("num")int num);

}
