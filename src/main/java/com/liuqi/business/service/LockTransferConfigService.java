package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.LockTransferConfigModel;
import com.liuqi.business.model.LockTransferConfigModelDto;

public interface LockTransferConfigService extends BaseService<LockTransferConfigModel,LockTransferConfigModelDto>{

    LockTransferConfigModelDto getByCurrencyId(Long currencyId);

}
