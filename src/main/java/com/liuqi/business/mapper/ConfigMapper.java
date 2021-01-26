package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.ConfigModel;
import com.liuqi.business.model.ConfigModelDto;


public interface ConfigMapper extends BaseMapper<ConfigModel,ConfigModelDto>{


    ConfigModelDto queryByName(String name);
}
