package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.LockWalletUpdateLogModel;
import com.liuqi.business.model.LockWalletUpdateLogModelDto;

import java.math.BigDecimal;

public interface LockWalletUpdateLogService extends BaseService<LockWalletUpdateLogModel,LockWalletUpdateLogModelDto>{

    /**
     * 添加记录
     * @param oldLock
     * @param modifyLock
     * @param newLock
     * @param oldFreeze
     * @param modifyFreeze
     * @param newFreeze
     * @param adminId
     * @param userId
     * @param currencyId
     */
    void insert(BigDecimal oldLock, BigDecimal modifyLock, BigDecimal newLock,
                BigDecimal oldFreeze, BigDecimal modifyFreeze, BigDecimal newFreeze,
                Long adminId, Long userId, Long currencyId, String remark);
}
