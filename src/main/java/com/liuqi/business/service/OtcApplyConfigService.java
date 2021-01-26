package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.OtcApplyConfigModel;
import com.liuqi.business.model.OtcApplyConfigModelDto;

public interface OtcApplyConfigService extends BaseService<OtcApplyConfigModel,OtcApplyConfigModelDto>{


    OtcApplyConfigModelDto getConfig();


}
