package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.VersionModel;
import com.liuqi.business.model.VersionModelDto;

public interface VersionService extends BaseService<VersionModel,VersionModelDto> {


    VersionModelDto getConfig();
}
