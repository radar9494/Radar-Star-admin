package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.UserWalletUpdateLogModel;
import com.liuqi.business.model.UserWalletUpdateLogModelDto;

import java.math.BigDecimal;

public interface UserWalletUpdateLogService extends BaseService<UserWalletUpdateLogModel,UserWalletUpdateLogModelDto>{

    /**
     * 添加记录
     * @param oldUsing
     * @param modifyUsing
     * @param newUsing
     * @param oldFreeze
     * @param modifyFreeze
     * @param newFreeze
     * @param adminId
     * @param userId
     * @param currencyId
     */
    void insert(BigDecimal oldUsing,BigDecimal modifyUsing, BigDecimal newUsing,
                BigDecimal oldFreeze, BigDecimal modifyFreeze, BigDecimal newFreeze,
                Long adminId,Long userId,Long currencyId,String remark,Integer type);
}
