package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.TransferLogModel;
import com.liuqi.business.model.TransferLogModelDto;

import java.math.BigDecimal;

public interface TransferLogService extends BaseService<TransferLogModel,TransferLogModelDto>{


    void transfer(Long userId, Long currencyId, Integer type, BigDecimal quantity);
}
