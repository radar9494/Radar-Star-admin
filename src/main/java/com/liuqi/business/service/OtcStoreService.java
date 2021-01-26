package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.OtcStoreModel;
import com.liuqi.business.model.OtcStoreModelDto;

public interface OtcStoreService extends BaseService<OtcStoreModel,OtcStoreModelDto> {

    OtcStoreModelDto getByUserId(Long userId, Long currencyId);

    OtcStoreModelDto init(Long userId, Long currencyId);

    void addTotal(Long userId, Long currencyId);

    void addSuccess(Long userId, Long currencyId);

}
