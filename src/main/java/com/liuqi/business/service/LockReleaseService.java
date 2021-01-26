package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.LockReleaseModel;
import com.liuqi.business.model.LockReleaseModelDto;

import java.math.BigDecimal;
import java.util.Date;

public interface LockReleaseService extends BaseService<LockReleaseModel,LockReleaseModelDto>{

    /**
     * 创建一个锁仓释放记录
     * @param recordId
     * @param tradeType
     */
    Long  createRelease(Long recordId,Integer tradeType);


    /**
     * 锁仓释放
     * @param id
     */
    void recordRelease(Long id);

    LockReleaseModelDto getByDate(Long userId,Long recordId, Integer tradeType, Date date);

    /**
     * 今天已释放数量
     * @param userId
     * @param currencyId
     * @param tradeType
     * @return
     */
    BigDecimal getTodayQuantityByDate(Long userId, Long currencyId, Integer tradeType);
}
