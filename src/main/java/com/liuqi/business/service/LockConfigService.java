package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.ReleaseConfigDto;
import com.liuqi.business.dto.SelectDto;
import com.liuqi.business.model.LockConfigModel;
import com.liuqi.business.model.LockConfigModelDto;

import java.util.Date;
import java.util.List;

public interface LockConfigService extends BaseService<LockConfigModel,LockConfigModelDto>{

    /**
     * 获取锁仓币种id列表
     * @return
     */
    List<Long> getLockCurrencyIdList();
    /**
     * 获取锁仓交易对id列表
     * @return
     */
    List<Long> getLockTradeIdList();

    List<SelectDto> getLockCurrencyList();

    List<SelectDto> getLockTradeList();

    LockConfigModelDto getByCurrencyId(Long currencyId);

    LockConfigModelDto getByTradeId(Long tradeId);

    /**
     * 是否能释放
     *          （开关和时间）
     *
     * @param tradeId
     * @param tradeType
     * @param date
     * @return
     */
    boolean canRelease(Long tradeId,Integer tradeType, Date date);
    /**
     * 是否能释放
     *      （开关和时间）
     *
     * @param config
     * @param tradeType
     * @param date
     * @return
     */
    boolean canRelease(LockConfigModel config,Integer tradeType, Date date);

    /**
     * 是否锁仓数据
     * @param tradeId
     * @return
     */
    boolean isLock(Long tradeId);
}
