package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.CurrencyConfigModel;
import com.liuqi.business.model.CurrencyConfigModelDto;

public interface CurrencyConfigService extends BaseService<CurrencyConfigModel, CurrencyConfigModelDto> {


    boolean currencyConfigAdd(Long currencyId);

    CurrencyConfigModelDto getByCurrencyId(Long id);
}
