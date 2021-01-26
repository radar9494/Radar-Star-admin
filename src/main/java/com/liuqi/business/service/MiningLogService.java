package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.MiningLogModel;
import com.liuqi.business.model.MiningLogModelDto;

import java.math.BigDecimal;

public interface MiningLogService extends BaseService<MiningLogModel,MiningLogModelDto>{

    MiningLogModel addLog(long userId, BigDecimal num, long currencyId, String currencyName, byte type);

}
