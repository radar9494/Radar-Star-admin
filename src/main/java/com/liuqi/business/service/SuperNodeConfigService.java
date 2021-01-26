package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.SuperNodeConfigModel;
import com.liuqi.business.model.SuperNodeConfigModelDto;

public interface SuperNodeConfigService extends BaseService<SuperNodeConfigModel, SuperNodeConfigModelDto> {

    SuperNodeConfigModelDto getConfig();

}
