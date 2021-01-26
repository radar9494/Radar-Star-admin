package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.OtcConfigModel;
import com.liuqi.business.model.OtcConfigModelDto;

public interface OtcConfigService extends BaseService<OtcConfigModel,OtcConfigModelDto>{


    OtcConfigModelDto getByCurrencyId(Long currencyId);
}
