package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.CurrencyDataModel;
import com.liuqi.business.model.CurrencyDataModelDto;

public interface CurrencyDataService extends BaseService<CurrencyDataModel,CurrencyDataModelDto>{

    /**
     * 初始化
     * @param currencyId
     */
    void init(Long currencyId);

}
