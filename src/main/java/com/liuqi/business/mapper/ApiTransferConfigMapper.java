package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.ApiTransferConfigModel;
import com.liuqi.business.model.ApiTransferConfigModelDto;


public interface ApiTransferConfigMapper extends BaseMapper<ApiTransferConfigModel,ApiTransferConfigModelDto>{


    ApiTransferConfigModelDto getByName(String name);
}
