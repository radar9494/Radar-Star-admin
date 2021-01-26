package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.LockConfigModel;
import com.liuqi.business.model.LockConfigModelDto;

import java.util.List;


public interface LockConfigMapper extends BaseMapper<LockConfigModel,LockConfigModelDto>{


    LockConfigModelDto getByCurrencyId(Long currencyId);

    LockConfigModelDto getByTradeId(Long tradeId);

    List<Long> getLockCurrencyIdList();

    List<Long> getLockTradeIdList();
}
