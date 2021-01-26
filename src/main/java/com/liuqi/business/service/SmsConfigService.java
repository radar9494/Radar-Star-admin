package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.SmsConfigModel;
import com.liuqi.business.model.SmsConfigModelDto;

public interface SmsConfigService extends BaseService<SmsConfigModel,SmsConfigModelDto> {

    SmsConfigModelDto getConfig();

    String getSign();

    String getKey();

    String getSecret();

    String getGjKey();
}
