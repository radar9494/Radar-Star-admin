package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.MiningConfigModel;
import com.liuqi.business.model.MiningConfigModelDto;

public interface MiningConfigService extends BaseService<MiningConfigModel,MiningConfigModelDto>{

    MiningConfigModel findConfig(Integer type,Long currencyId);

}
