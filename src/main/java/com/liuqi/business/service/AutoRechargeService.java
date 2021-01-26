package com.liuqi.business.service;

import com.liuqi.business.dto.chain.TxInfo;
import com.liuqi.business.model.CurrencyModel;
import com.liuqi.business.model.RechargeModel;

import java.util.List;

public interface AutoRechargeService {

    /**
     * 检查币种充值
     * @param currency
     */
    void check(CurrencyModel currency, Integer protocol, String thirdCurrency,Integer confirm);

    /**
     * 检查指定区块币种充值
     * @param currencyId
     * @param thirdCurrency
     * @param block
     */
    void checkBlock(Long currencyId,Integer protocol,String thirdCurrency,Long block);

    /**
     * 推送汇总
     * @param rechargeModel
     */
    void send(RechargeModel rechargeModel);
    /**
     * 获取区块高度
     * @param currency
     * @return
     */
    Long block(CurrencyModel currency, String thirdCurrency);

    /**
     * 获取交易数据
     * @param thirdCurrency
     * @param block
     * @param fromAddress  EOS充值地址
     * @return
     */
    List<TxInfo> getByBlock(String thirdCurrency, Long block, Long endBlock, String fromAddress);
}
