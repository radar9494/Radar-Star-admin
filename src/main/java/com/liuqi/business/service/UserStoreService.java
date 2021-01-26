package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.UserStoreModel;
import com.liuqi.business.model.UserStoreModelDto;

import java.math.BigDecimal;
import java.util.List;

public interface UserStoreService extends BaseService<UserStoreModel,UserStoreModelDto>{

    /**
     * 获取下一个匹配的承运商
     * @param currencyId
     * @param quantity
     * @param tradeType
     * @return
     */
    UserStoreModelDto getNextMatchStore(Long currencyId,BigDecimal quantity,Integer tradeType);

    List<UserStoreModelDto> getUsing();

}
