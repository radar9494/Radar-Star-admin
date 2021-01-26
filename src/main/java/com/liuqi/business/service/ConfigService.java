package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.ConfigModel;
import com.liuqi.business.model.ConfigModelDto;

public interface ConfigService extends BaseService<ConfigModel, ConfigModelDto> {

    String queryValueByName(String s);

    ConfigModelDto queryByName(String s);

    String getProjectAddress();

    String getProjectName();
}
