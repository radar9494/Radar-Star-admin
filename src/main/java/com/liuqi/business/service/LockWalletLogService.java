package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.LockWalletLogModel;
import com.liuqi.business.model.LockWalletLogModelDto;
import com.liuqi.business.model.LockWalletModel;

import java.math.BigDecimal;

public interface LockWalletLogService extends BaseService<LockWalletLogModel, LockWalletLogModelDto> {


    /**
     * * 添加日志
     *
     * @param userId
     * @param currencyId
     * @param money      操作金额
     * @param type       LoclWalletType
     * @param remarks    备注
     */
    void addLog(Long userId, Long currencyId, BigDecimal money, Integer type,Long orderId, String remarks, LockWalletModel model);
}
