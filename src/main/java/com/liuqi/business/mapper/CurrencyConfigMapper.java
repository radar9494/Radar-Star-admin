package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.CurrencyConfigModel;
import com.liuqi.business.model.CurrencyConfigModelDto;


public interface CurrencyConfigMapper extends BaseMapper<CurrencyConfigModel, CurrencyConfigModelDto> {

    CurrencyConfigModelDto getByCurrencyId(Long currencyId);
}
