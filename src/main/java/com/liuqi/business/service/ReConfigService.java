package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.ReConfigModel;
import com.liuqi.business.model.ReConfigModelDto;

public interface ReConfigService extends BaseService<ReConfigModel,ReConfigModelDto>{

    ReConfigModelDto getConfig();
}
