package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.RaiseConfigModel;
import com.liuqi.business.model.RaiseConfigModelDto;
import com.liuqi.response.ReturnResponse;

public interface RaiseConfigService extends BaseService<RaiseConfigModel,RaiseConfigModelDto>{


    ReturnResponse start(Long id);

    ReturnResponse end(Long id);
}
