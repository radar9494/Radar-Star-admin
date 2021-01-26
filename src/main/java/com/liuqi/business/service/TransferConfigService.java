package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.TransferConfigModel;
import com.liuqi.business.model.TransferConfigModelDto;

public interface TransferConfigService extends BaseService<TransferConfigModel,TransferConfigModelDto>{


    TransferConfigModelDto getByCurrencyId(Long currencyId);
}
