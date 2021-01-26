package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.CurrencyTradeModel;
import com.liuqi.business.model.CurrencyTradeModelDto;

import java.util.List;
import java.util.Set;


public interface CurrencyTradeMapper extends BaseMapper<CurrencyTradeModel,CurrencyTradeModelDto>{


    /**
     * 获取交易币种的所有交易对id
     * @param currencyId
     * @return
     */
    Set<Long> getTradeIdByCurrencyId(Long currencyId);
    /**
     * 获取交易币种的所有交易对id
     * @param currencyId
     * @return
     */
    Set<Long> getTradeIdByTradeCurrencyId(Long currencyId);
}
