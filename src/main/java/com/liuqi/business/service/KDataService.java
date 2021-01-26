package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.KDto;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.model.KDataModel;
import com.liuqi.business.model.KDataModelDto;
import org.springframework.dao.TransientDataAccessException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface KDataService extends BaseService<KDataModel,KDataModelDto>{

    /**
     * 生成K线图
     * @param type
     * @param startTime
     * @param endTime
     */
    void storeKChartData(CurrencyTradeModelDto trade, Integer type, Date startTime, Date endTime);

    /**
     * 查询交易k线数据
     * @param type
     * @param tradeId
     * @return
     */
    List<KDto> queryDataByType(Integer type, Long tradeId, Date startTime, Date endTime);

    /**
     * 初始化缓存
     * @param type
     * @param tradeId
     * @return
     */
    void initCache(Integer type, Long tradeId);
    /**
     * 添加到缓存
     * @param type
     * @param tradeId
     * @return
     */
    void addCache(Integer type, Long tradeId,KDataModel kDataModel);

    List<KDto> queryCacheDataByType(Integer type, Long tradeId, Date startTime, Date endTime);
    /**
     * 获取最后一条数据
     * @param type
     * @param tradeId
     * @return
     */
    KDto getLast(Integer type,Long tradeId);
    /**
     * 缓存当前的K线数据
     * @param tradeId
     * @param price
     * @param quantity
     */
    void addCacheCurK(Long tradeId, BigDecimal price, BigDecimal quantity);

    /**
     * 获取当前K线
     * @param type
     * @param tradeId
     */
    KDto getCacheCurK(Integer type,Long tradeId);


    /**
     * 获取最后一条数据
     * @param type
     * @param tradeId
     * @return
     */
    KDataModelDto getLastByDb(Integer type,Long tradeId);

    /**
     * 获取当前时间K线
     * @param type
     * @param tradeId
     */
    KDataModel getKByDate(Integer type, Long tradeId,Date date);
}
