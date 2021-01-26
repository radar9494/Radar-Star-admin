package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.TransferModel;
import com.liuqi.business.model.TransferModelDto;

import java.math.BigDecimal;

public interface TransferService extends BaseService<TransferModel,TransferModelDto>{


    void transfer(Long userId, Long id, Long currencyId, BigDecimal quantity,String phone);
}
