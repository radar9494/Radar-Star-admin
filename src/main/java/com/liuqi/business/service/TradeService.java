package com.liuqi.business.service;


import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.model.TradeRecordModelDto;
import com.liuqi.business.model.TrusteeModelDto;

import java.math.BigDecimal;
import java.util.Map;

public interface TradeService {

    /**
     * 买单处理
     * @param trade
     * @param buyId
     * @param sellId
     * @param tradeType 交易类型
     * @return 记录id
     */
    TradeRecordModelDto doTrade(CurrencyTradeModelDto trade,Long buyId,Long sellId, Integer tradeType);

    /**
     * 取消买单
     * @param trade
     * @param trusteeId
     * @return
     */
    void doCancelBuyTrade(CurrencyTradeModelDto trade, Long trusteeId);

    /**
     * 取消买单操作
     * @param trade
     * @param trusteeId
     * @return
     */
    void doCancelSellTrade(CurrencyTradeModelDto trade, Long trusteeId);

    /**
     * 处理交易对信息
     * @param record
     */
    void doTradeInfo(TradeRecordModelDto record);
    /**
     * 查询交易对 交易信息
     * @param trade
     * @return
     */
    TradeInfoDto getByCurrencyAndTradeType(CurrencyTradeModelDto trade);

    /**
     * 获取开盘价
     * @return
     */
    BigDecimal getOpenPrice(Long tradeId);

    /**
     * 获取交易对当前价格
     * @param tradeId
     * @return
     */
    BigDecimal getPriceByTradeId(Long tradeId);
    /**
     * 获取币种价格
     * @param currencyId
     * @return
     */
    BigDecimal getPriceByCurrencyId(Long currencyId);

    /**
     * 获取所有币种价格
     * @return
     */
    Map<String,String> getAllPrice();

    /**
     * 交易钱包处理
     * @param recordId
     */
    void tradeBuyWallet(Long recordId);
    /**
     * 交易钱包处理
     * @param recordId
     */
    void tradeSellWallet(Long recordId);



}
