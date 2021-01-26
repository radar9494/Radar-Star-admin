package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.OtcConfigModel;
import com.liuqi.business.model.OtcConfigModelDto;


public interface OtcConfigMapper extends BaseMapper<OtcConfigModel,OtcConfigModelDto>{


    OtcConfigModelDto getByCurrencyId(Long currencyId);
}
