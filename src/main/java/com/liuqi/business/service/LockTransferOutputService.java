package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.LockTransferConfigModelDto;
import com.liuqi.business.model.LockTransferOutputModel;
import com.liuqi.business.model.LockTransferOutputModelDto;

import java.math.BigDecimal;
import java.util.Date;

public interface LockTransferOutputService extends BaseService<LockTransferOutputModel,LockTransferOutputModelDto>{

    /**
     * 申请转出
     * @param config
     * @param userId
     * @param receiveUserId
     * @param applyQuantity
     * @return
     */
    LockTransferOutputModel publish(LockTransferConfigModelDto config, Long userId, Long receiveUserId, BigDecimal applyQuantity);

    /**
     * 获取今天用户转出次数
     * @param currencyId
     * @param userId
     * @return
     */
    int getTodayTimes(Long currencyId,Long userId);

    /**
     * 获取时间段用户转出次数
     * @param currencyId
     * @param userId
     * @return
     */
    int getTodayTimes(Long currencyId, Long userId, Date startTime,Date endTime);
}
