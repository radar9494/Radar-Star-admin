package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.LockTransferConfigModelDto;
import com.liuqi.business.model.LockTransferInputModel;
import com.liuqi.business.model.LockTransferInputModelDto;

import java.math.BigDecimal;

public interface LockTransferInputService extends BaseService<LockTransferInputModel,LockTransferInputModelDto>{

    /**
     * 申请转入
     * @param config
     * @param userId
     * @param applyQuantity
     * @return
     */
    LockTransferInputModel publish(LockTransferConfigModelDto config, Long userId, BigDecimal applyQuantity);

}
