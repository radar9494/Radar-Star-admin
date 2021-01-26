package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.CtcConfigModel;
import com.liuqi.business.model.CtcConfigModelDto;


public interface CtcConfigMapper extends BaseMapper<CtcConfigModel,CtcConfigModelDto>{

    /**
     * 根据币种获取
     * @param currencyId
     * @return
     */
    CtcConfigModelDto getByCurrencyId(Long currencyId);
}
