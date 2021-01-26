package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.LockTransferConfigModel;
import com.liuqi.business.model.LockTransferConfigModelDto;


public interface LockTransferConfigMapper extends BaseMapper<LockTransferConfigModel,LockTransferConfigModelDto>{


    LockTransferConfigModelDto getByCurrencyId(Long currencyId);
}
