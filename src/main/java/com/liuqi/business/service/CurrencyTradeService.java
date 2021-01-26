package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.CurrencyTradeModel;
import com.liuqi.business.model.CurrencyTradeModelDto;

import java.util.List;

public interface CurrencyTradeService extends BaseService<CurrencyTradeModel, CurrencyTradeModelDto> {

    String getNameById(Long tradeId);
    /**
     * 获取交易区的交易对
     * @param areaId
     */
    List<CurrencyTradeModelDto> getTradeInfoByArea(Long areaId);
    /**
     * 获取交易区的可用交易对
     * @param areaId
     */
    List<CurrencyTradeModelDto> getCanUseTradeInfoByArea(Long areaId);
    /**
     * 获取交易对信息
     * @param currencyId
     * @param tradeCurrencyId
     * @return
     */
    CurrencyTradeModelDto getByCurrencyId(Long currencyId,Long tradeCurrencyId);
    /**
     * 获取交易对信息
     * @param currencyName
     * @param tradeCurrencyName
     * @return
     */
    CurrencyTradeModelDto getByCurrencyName(String currencyName,String tradeCurrencyName);
    /**
     * 获取所有关联的交易对
     * @param currencyId
     * @return
     */
    List<Long> getAllRelevanceTradeId(Long currencyId);


    /**
     * 获取所有的交易对id   XXX/currencyid
     * @param currencyId
     * @return
     */
    List<Long> getTradeIdListByCurrencyId(Long currencyId);


    /**
     * 获取所有的交易对id   tradeCurrencyId/xxx
     * @param tradeCurrencyId
     * @return
     */
    List<Long> getTradeIdListByTradeCurrencyId(Long tradeCurrencyId);


}
