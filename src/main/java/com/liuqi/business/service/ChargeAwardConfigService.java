package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.ChargeAwardConfigModel;
import com.liuqi.business.model.ChargeAwardConfigModelDto;

public interface ChargeAwardConfigService extends BaseService<ChargeAwardConfigModel,ChargeAwardConfigModelDto>{

    ChargeAwardConfigModelDto getConfig();


}
